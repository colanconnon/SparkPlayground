import java.sql.{Connection, DriverManager}

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{ForeachWriter, SparkSession}
import org.apache.spark.sql.streaming.{OutputMode, Trigger}


object streaming {
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


    words.writeStream.foreach(new ForeachWriter[String] {
      private var connection: Connection = null

      def open(partitionId: Long, version: Long): Boolean = {
        // open connection
        val jdbcHostname = "mssql"
        val jdbcPort = 1433
        val jdbcDatabase = "master"
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
        val jdbcUrl = s"jdbc:sqlserver://${jdbcHostname}:${jdbcPort};database=${jdbcDatabase}"
        this.connection = DriverManager.getConnection(jdbcUrl, "sa", "YourStrong!Passw0rd")
        this.connection.setAutoCommit(true)
        println("Here")
        !connection.isClosed
      }

      def process(record: String) = {
        println("HERE")
        val stmt = this.connection.prepareStatement("insert into word (word) values (?)")
        println(record)
        stmt.setString(1, record)
        stmt.execute()
      }

      def close(errorOrNull: Throwable): Unit = {
        this.connection.close()
      }
    })
      .start.awaitTermination()
  }

}