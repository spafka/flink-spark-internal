package com.github.spafka

import java.io.File

import org.apache.spark.sql.{Row, SaveMode, SparkSession}

object HIveApp {


  // $example on:spark_hive$
  case class Record(key: Int, value: String)

  // $example off:spark_hive$
  def main(args: Array[String]): Unit = {
    val warehouseLocation = new File("spark-warehouse").getAbsolutePath

    val spark = SparkSession.builder().appName("Spark Hive Example").master("local[*]").config("spark.sql.warehouse.dir", warehouseLocation).enableHiveSupport().getOrCreate()

    import spark.implicits._
    import spark.sql

    sql("CREATE TABLE IF NOT EXISTS src (key INT, value STRING) USING hive")
    sql("LOAD DATA LOCAL INPATH 'spark/data-adhoc/src/main/resources/kv1.txt' INTO TABLE src")

    // Queries are expressed in HiveQL
    sql("SELECT * FROM src").show() // Aggregation queries are also supported.
    sql("SELECT COUNT(*) FROM src").show()

    // The results of SQL queries are themselves DataFrames and support all normal functions.
    val sqlDF = sql("SELECT key, value FROM src WHERE key < 10 ORDER BY key")

    // The items in DataFrames are of type Row, which allows you to access each column by ordinal.
    val stringsDS = sqlDF.map { case Row(key: Int, value: String) => s"Key: $key, Value: $value"
    }
    stringsDS.show()
    // You can also use DataFrames to create temporary views within a SparkSession.
    val recordsDF = spark.createDataFrame((1 to 100).map(i => Record(i, s"val_$i")))
    recordsDF.createOrReplaceTempView("records")

    // Queries can then join DataFrame data with data stored in Hive.
    sql("SELECT * FROM records r JOIN src s ON r.key = s.key").show()
    sql("CREATE TABLE hive_records(key int, value string) STORED AS PARQUET")
    // Save DataFrame to the Hive managed table
    val df = spark.table("src")
    df.write.mode(SaveMode.Overwrite).saveAsTable("hive_records") // After insertion, the Hive managed table has data now
    sql("SELECT * FROM hive_records").show()

    // Prepare a Parquet data directory
    val dataDir = "/tmp/parquet_data"
    spark.range(10).write.parquet(dataDir) // Create a Hive external Parquet table
    sql(s"CREATE EXTERNAL TABLE hive_ints(key int) STORED AS PARQUET LOCATION '$dataDir'") // The Hive external table should already have data
    sql("SELECT * FROM hive_ints").show()
    spark.sqlContext.setConf("hive.exec.dynamic.partition", "true")
    spark.sqlContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")
    df.write.partitionBy("key").format("hive").saveAsTable("hive_part_tbl")
    sql("SELECT * FROM hive_part_tbl").show()


    spark.stop()

  }
}
