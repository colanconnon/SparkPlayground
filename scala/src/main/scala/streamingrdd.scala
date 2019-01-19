import java.util.Properties

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object streamingrdd {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .appName("StructuredNetworkWordCount")
      .getOrCreate()
    import spark.implicits._

    val lines = spark.readStream
      .format("socket")
      .option("host", "host.docker.internal")
      .option("port", 9999)
      .load()

    val words = lines.as[String].flatMap(_.split(" "))

    words.writeStream.foreachBatch { (batchDF: Dataset[String], batchId: Long) =>
      val connectionProperties = new Properties()
      val jdbcHostname = "mssql"
      val jdbcPort = 1433
      val jdbcDatabase = "master"
      val jdbcUrl = s"jdbc:sqlserver://${jdbcHostname}:${jdbcPort};database=${jdbcDatabase}"
      val driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
      connectionProperties.setProperty("Driver", driverClass)
      connectionProperties.setProperty("AutoCommit", "true")
      connectionProperties.put("user", "sa")
      connectionProperties.put("password", "YourStrong!Passw0rd")
      batchDF.write.jdbc(jdbcUrl, "words", connectionProperties)
    }
  }
}