from pyspark.sql import SparkSession
from pyspark.sql.functions import explode
from pyspark.sql.functions import split

spark = SparkSession \
    .builder \
    .appName("StructuredNetworkWordCount") \
    .getOrCreate()

sc = spark.sparkContext

logger = sc._jvm.org.apache.log4j
logger.LogManager.getLogger("org"). setLevel( logger.Level.ERROR )
logger.LogManager.getLogger("akka").setLevel( logger.Level.ERROR )


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
)

wordCount = words.groupBy('word').count()

def handle_write(df, batchId):
    df.show()
    props = {
        "driver": "com.microsoft.sqlserver.jdbc.SQLServerDriver",
        "user": "sa",
        "password": "YourStrong!Passw0rd"
    }
    jdbcUrl = "jdbc:sqlserver://mssql:1433;database=master"
    df.write.jdbc(jdbcUrl, "wordCount", "overwrite", props)

query = wordCount \
    .writeStream \
    .outputMode("complete") \
    .foreachBatch(handle_write) \
    .start()

query.awaitTermination()