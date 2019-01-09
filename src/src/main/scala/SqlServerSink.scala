import java.util.Properties

import org.apache.spark.sql.execution.streaming.Sink
import org.apache.spark.internal.Logging
import org.apache.spark.sql.sources.{DataSourceRegister, StreamSinkProvider}
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.{DataFrame, ForeachWriter, SQLContext, SaveMode}

class SqlServerSink(options: Map[String, String]) extends Sink with Logging {

  private val dbHost = options.get("dbHost").map(_.toString).getOrElse("")
  private val table = options.get("table").map(_.toString).getOrElse("")
  private val dbPort = options.get("dbPort").map(_.toString).getOrElse("")
  private val dbDatabase = options.get("dbDatabase").map(_.toString).getOrElse("")
  private val dbUser = options.get("dbUser").map(_.toString).getOrElse("")
  private val dbPassword = options.get("dbPassword").map(_.toString).getOrElse("")

  override def addBatch(batchId: Long, data: DataFrame): Unit = synchronized {
    val df = data.sparkSession.createDataFrame(data.rdd, data.schema)
    val connectionProperties = new Properties()
    val jdbcUrl = s"jdbc:sqlserver://${this.dbHost}:${this.dbPort};database=${this.dbDatabase}"
    val driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
    connectionProperties.setProperty("Driver", driverClass)
    connectionProperties.setProperty("AutoCommit", "true")
    connectionProperties.put("user", s"${this.dbUser}")
    connectionProperties.put("password", s"${this.dbPassword}")
    df.write.mode(SaveMode.Append).jdbc(jdbcUrl, this.table, connectionProperties)
  }
}

class SqlServerSinkProvider extends StreamSinkProvider with DataSourceRegister {
  def createSink(
                  sqlContext: SQLContext,
                  parameters: Map[String, String],
                  partitionColumns: Seq[String],
                  outputMode: OutputMode): Sink = {
    new SqlServerSink(parameters)
  }

  def shortName(): String = "SqlServer"
}

