
from pyspark.sql import SparkSession
from pyspark.sql.functions import explode
from pyspark.sql.functions import split, udf, col

spark = SparkSession \
    .builder \
    .appName("StructuredNetworkWordCount") \
    .getOrCreate()


@udf()
def first_char(word):
    return word[0] if len(word) > 0 else ''

sc = spark.sparkContext

logger = sc._jvm.org.apache.log4j
logger.LogManager.getLogger("org"). setLevel( logger.Level.ERROR )
logger.LogManager.getLogger("akka").setLevel( logger.Level.ERROR )

lines = spark.readStream \
  .format("kafka") \
  .option("subscribe", "test") \
  .option("startingOffsets", "earliest") \
  .option("group.id", "test-group") \
  .option("kafka.bootstrap.servers", "kafka:9092") \
  .load()
  
lines = lines.selectExpr("CAST(value AS STRING)")

lines = lines.select(
   explode(
       split(lines.value, " ")
   ).alias("word")
).select('word', first_char(col('word')).alias('first_char'))

query = lines \
    .writeStream \
    .outputMode("append") \
    .format("console") \
    .start()

query.awaitTermination()