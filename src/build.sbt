name := "Simple Project"

version := "1.0"

scalaVersion := "2.11.12"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.1"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.3.1"
libraryDependencies += "com.microsoft.sqlserver" % "mssql-jdbc" % "7.0.0.jre8" % Test
