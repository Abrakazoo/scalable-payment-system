from pyspark.sql.types import StructType, StructField, StringType, FloatType
from pyspark.sql import SparkSession
import pyspark.sql.functions as F
import configparser
import aggregate_windows
import os
import pathlib

CONFIG_FILE =  pathlib.Path(__file__).parent.absolute() / "config.ini"
print(CONFIG_FILE)
KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
KAFKA_TOPIC = "payments"
KAFKA_RISK_TOPIC = "risks"
ENCODING = "iso-8859-1"

conf = configparser.ConfigParser()
conf.read(CONFIG_FILE)
duration = conf['DEFAULT']['duration']
interval = conf['DEFAULT']['interval']
watermark = conf['DEFAULT']['watermark']

SCHEMA = StructType([
    StructField("payment_id", StringType()),      
    StructField("amount", FloatType()),      
    StructField("currency", StringType()),             
    StructField("payer_country", StringType()),        
    StructField("payerId", StringType()),             
    StructField("payeeId", StringType()),          
    StructField("paymentMethodId", StringType()),
    StructField("orderId", StringType())
])

spark = SparkSession.builder.appName("read_test_straeam").getOrCreate()

# Reduce logging
spark.sparkContext.setLogLevel("WARN")

# Read kafka stream
df = spark.readStream.format("kafka") \
    .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS) \
    .option("subscribe", KAFKA_TOPIC) \
    .option("startingOffsets", "earliest") \
    .option("includeHeaders", "true") \
    .option("failOnDataLoss", "false") \
    .load()

df = df.selectExpr("key", 
                   "value", 
                   "timestamp") \
       .select("key", 
               F.from_json(F.decode(F.col("value"), 
                                    ENCODING), 
                           SCHEMA).alias("value"),
               "timestamp") \
        .select("key", "timestamp", "value.*") \
            .withWatermark("timestamp", watermark)

# Aggregate and encode for publishing
riskFlaggedDf = aggregate_windows.payer_aggs(df, duration, interval) \
    .withColumn("value", F.to_json(F.struct(F.col("*")) ) )\
    .withColumn("value", F.encode(F.col("value"), ENCODING).cast("binary"))

# Show computations on multiple aggregations (e.g., payee aggregations).
# this approach, while not supported in structured stream processing 
# can be used to capture all these and more calculations and persist to 
# db, storage, send via http, etc., and also allows for debugging.
def batchProcess(batchDf, batchId):
    batchDf.persist()
    aggregate_windows.payee_aggs(batchDf, duration, interval).show(truncate=False)
    aggregate_windows.payer_aggs(batchDf, duration, interval).show(truncate=False)
    aggregate_windows.total_amount(batchDf, duration, interval).show(truncate=False)
    batchDf.unpersist()

# # Publish to kafka stream. 
# # Note: for update/append modes, sorting should be disabled
riskFlaggedDf.writeStream \
  .format("kafka") \
  .outputMode("complete") \
  .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS) \
  .option("topic", KAFKA_RISK_TOPIC) \
  .option("checkpointLocation", "/tmp/checkpoint")\
  .start() \
  .awaitTermination()


# Uncomment to stream all calculations on console
# df.writeStream \
#   .format("console") \
#   .outputMode("update") \
#   .foreachBatch(batchProcess) \
#   .start() \
#   .awaitTermination()