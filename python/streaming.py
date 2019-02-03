from pyspark.sql import SparkSession
from pyspark.sql.functions import explode
from pyspark.sql.functions import split, udf, col, monotonically_increasing_id
import json


spark = SparkSession \
    .builder \
    .appName("StructuredNetworkWordCount") \
    .getOrCreate()

sc = spark.sparkContext

hbase_catalog = json.dumps({
    "table": {
        "namespace": "default",
        "name": "test_table",
        "tablecoder": "PrimitiveType"
    },
    "rowkey": "word",
    "columns": {
        "word": {"cf": "rowkey", "col": "word", "type": "string"},
        "first_char": {"cf": "0", "col": "first_char", "type": "string"}
    }
})

logger = sc._jvm.org.apache.log4j
logger.LogManager.getLogger("org"). setLevel(logger.Level.ERROR)
logger.LogManager.getLogger("akka").setLevel(logger.Level.ERROR)


@udf()
def first_char(word):
    return word[0] if len(word) > 0 else ''


lines = spark \
    .readStream \
    .format("socket") \
    .option("host", "host.docker.internal") \
    .option("port", 9999) \
    .load()

words = lines.select(
    explode(
        split(lines.value, " ")
    ).alias("word")
).select('word', first_char(col('word')).alias("first_char"))


def handle_sql_server_write(df, batchId):
    df.show()
    props = {
        "driver": "com.microsoft.sqlserver.jdbc.SQLServerDriver",
        "user": "sa",
        "password": "YourStrong!Passw0rd"
    }
    jdbcUrl = "jdbc:sqlserver://mssql:1433;database=master"
    df.write.jdbc(jdbcUrl, "wordCount", "overwrite", props)


def handle_hbase_write(df, batch_id):
    if df.count() > 0:
        df.show()
        df.write \
            .format("org.apache.spark.sql.execution.datasources.hbase") \
            .option("catalog", hbase_catalog) \
            .option("newtable", 5) \
            .save()


query = words \
    .writeStream \
    .outputMode("append") \
    .foreachBatch(handle_hbase_write) \
    .start()

query.awaitTermination()
