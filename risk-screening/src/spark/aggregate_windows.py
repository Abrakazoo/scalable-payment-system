import pyspark.sql.functions as F

# Sliding window length
# WINDOW_DURATION_MINUTES = 10
# Sliding window interval
# WINDOW_INTERVAL_MINUTES = 1

# Aggregate by payee
def payee_aggs(df, duration, interval):
    agg_df = df.groupBy(F.window("timestamp", duration, interval), "payeeId") \
                                     .agg(F.min("amount").alias("payee min mount"),
                                          F.max("amount").alias("payee max amount"),
                                          F.avg("amount").alias("payee avg amount"),
                                          F.sum("amount").alias("payee total revenue")) \
                                             .orderBy("window")
                                             
                        
    return agg_df

# Aggregate by payer
def payer_aggs(df, duration, interval):
    agg_df = df.groupBy(F.window("timestamp", duration, interval), "payerId") \
                                           .agg(F.sum("amount").alias("volume"),
                                                F.count("payer_country").alias("countries"),
                                                F.count("payeeId").alias("payments to payee")) \
                                                    .orderBy("window")
    agg_df = agg_df.withColumn("flagged - high volume",
            F.when(agg_df["volume"] > 10000, True) \
                .otherwise(False)) \
        .withColumn("flagged - many payments to payee",
            F.when(agg_df["payments to payee"] > 10, True) \
                .otherwise(False)) \
        .withColumn("flagged - multiple countries", \
            F.when(agg_df["countries"] > 1, True) \
                .otherwise(False))
    
    return agg_df


# Aggregate by window and summarize amounts
def total_amount(df, duration, interval):
    agg_df = df.groupBy(F.window("timestamp", duration, interval)) \
                                     .agg(F.sum("amount").alias("total")) \
                                         .orderBy("window")
                                         
    
    return agg_df