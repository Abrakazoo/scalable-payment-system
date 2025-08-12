import os
import pathlib
import configparser
from pyflink.table import (
    EnvironmentSettings, 
    TableEnvironment,
    DataTypes,
    Schema
)
import aggregate_windows as agg

# Configuration
CONFIG_FILE = pathlib.Path(__file__).parent.absolute() / "config.ini"
KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
KAFKA_TOPIC = "payments"
KAFKA_RISK_TOPIC = "risks"

# Read configuration
conf = configparser.ConfigParser()
conf.read(CONFIG_FILE)
duration = conf['DEFAULT']['duration']
interval = conf['DEFAULT']['interval']
watermark = conf['DEFAULT']['watermark']

# Create Table environment
env_settings = EnvironmentSettings.in_streaming_mode()
t_env = TableEnvironment.create(env_settings)

# Configure Kafka connector
t_env.get_config().set_string("pipeline.jars", "file:///path/to/flink-sql-connector-kafka.jar")
t_env.get_config().set_string("table.exec.state.ttl", watermark)

# Define source schema
source_schema = Schema.new_builder() \
    .column("payment_id", DataTypes.STRING()) \
    .column("amount", DataTypes.FLOAT()) \
    .column("currency", DataTypes.STRING()) \
    .column("payer_country", DataTypes.STRING()) \
    .column("payerId", DataTypes.STRING()) \
    .column("payeeId", DataTypes.STRING()) \
    .column("paymentMethodId", DataTypes.STRING()) \
    .column("orderId", DataTypes.STRING()) \
    .column("event_time", DataTypes.TIMESTAMP(3)) \
    .watermark("event_time", "event_time - INTERVAL '5' SECOND") \
    .build()

# Create source table
source_ddl = f"""
    CREATE TABLE payments (
        {source_schema.to_string()}
    ) WITH (
        'connector' = 'kafka',
        'topic' = '{KAFKA_TOPIC}',
        'properties.bootstrap.servers' = '{KAFKA_BOOTSTRAP_SERVERS}',
        'properties.group.id' = 'risk-screening-group',
        'scan.startup.mode' = 'earliest-offset',
        'format' = 'json'
    )
"""

# Create sink table for risk flags
sink_ddl = f"""
    CREATE TABLE risk_flags (
        window_start TIMESTAMP(3),
        window_end TIMESTAMP(3),
        payerId STRING,
        volume DOUBLE,
        countries BIGINT,
        payments_to_payee BIGINT,
        flagged_high_volume BOOLEAN,
        flagged_many_payments_to_payee BOOLEAN,
        flagged_multiple_countries BOOLEAN
    ) WITH (
        'connector' = 'kafka',
        'topic' = '{KAFKA_RISK_TOPIC}',
        'properties.bootstrap.servers' = '{KAFKA_BOOTSTRAP_SERVERS}',
        'format' = 'json'
    )
"""

t_env.execute_sql(source_ddl)
t_env.execute_sql(sink_ddl)

# Get source table
payments_table = t_env.from_path("payments")

# Calculate risk flags
risk_flags = agg.payer_aggs(payments_table, duration, interval)

# Insert into sink
risk_flags.execute_insert("risk_flags").wait()

# Optional: Print other aggregations (for development/debugging)
payee_stats = agg.payee_aggs(payments_table, duration, interval)
total_stats = agg.total_amount(payments_table, duration, interval)

print("Payee Statistics:")
payee_stats.print_schema()
payee_stats.execute().print()

print("Total Amount Statistics:")
total_stats.print_schema()
total_stats.execute().print()