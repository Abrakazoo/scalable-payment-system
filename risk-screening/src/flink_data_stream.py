import argparse
import logging
import sys

from pyflink.common import WatermarkStrategy, Encoder, Types
from pyflink.datastream import StreamExecutionEnvironment, RuntimeExecutionMode
from pyflink.datastream.connectors.file_system import FileSource, StreamFormat, FileSink, OutputFileConfig, RollingPolicy

from pyflink.datastream.connectors.kafka import KafkaSource, KafkaSink
from pyflink.datastream.connectors.kafka import KafkaDeserializationSchema, KafkaSerializationSchema
from pyflink.datastream.connectors.kafka import SimpleStringSchema
from pyflink.datastream.window import TumblingProcessingTimeWindows
from pyflink.datastream.functions import MapFunction, FlatMapFunction, ProcessWindowFunction
from pyflink.common.typeinfo import Types
from pyflink.common.watermark_strategy import WatermarkStrategy

env = StreamExecutionEnvironment.get_execution_environment()
env.set_runtime_mode(RuntimeExecutionMode.STREAMING)
env.set_parallelism(1)

ds = env.from_source(
    KafkaSource.builder()
        .set_bootstrap_servers("localhost:9092")
        .set_topics("payments")
        .set_value_only_deserializer(KafkaDeserializationSchema.value_only(SimpleStringSchema()))
        .set_starting_offsets("earliest")
        .build(),
    WatermarkStrategy.for_bounded_out_of_orderness(5000, Types.LONG()),
    "kafka_source"
)