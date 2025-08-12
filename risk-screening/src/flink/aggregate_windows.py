from pyflink.table import expressions as expr
from pyflink.table.expressions import col
from pyflink.table import DataTypes

def payee_aggs(table, duration, interval):
    """Aggregate payment data by payee over tumbling windows"""
    return table.window(
        expr.Tumble.over(duration).every(interval).on(col("event_time")).alias("window")
    ).group_by(col("window"), col("payeeId")) \
     .select(
        col("window").start.alias("window_start"),
        col("window").end.alias("window_end"),
        col("payeeId"),
        expr.min_(col("amount")).alias("payee_min_amount"),
        expr.max_(col("amount")).alias("payee_max_amount"),
        expr.avg(col("amount")).alias("payee_avg_amount"),
        expr.sum_(col("amount")).alias("payee_total_revenue")
    )

def payer_aggs(table, duration, interval):
    """Aggregate payment data by payer over tumbling windows with risk flags"""
    base_aggs = table.window(
        expr.Tumble.over(duration).every(interval).on(col("event_time")).alias("window")
    ).group_by(col("window"), col("payerId")) \
     .select(
        col("window").start.alias("window_start"),
        col("window").end.alias("window_end"),
        col("payerId"),
        expr.sum_(col("amount")).alias("volume"),
        expr.count_distinct(col("payer_country")).alias("countries"),
        expr.count_distinct(col("payeeId")).alias("payments_to_payee")
    )

    return base_aggs.select(
        "*",
        (col("volume") > 10000).alias("flagged_high_volume"),
        (col("payments_to_payee") > 10).alias("flagged_many_payments_to_payee"),
        (col("countries") > 1).alias("flagged_multiple_countries")
    )

def total_amount(table, duration, interval):
    """Aggregate total payment amounts over tumbling windows"""
    return table.window(
        expr.Tumble.over(duration).every(interval).on(col("event_time")).alias("window")
    ).group_by(col("window")) \
     .select(
        col("window").start.alias("window_start"),
        col("window").end.alias("window_end"),
        expr.sum_(col("amount")).alias("total")
    )