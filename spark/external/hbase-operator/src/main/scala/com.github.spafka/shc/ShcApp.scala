package com.github.spafka.shc

import org.apache.spark.SparkConf
import org.apache.spark.sql.execution.datasources.hbase.{HBaseRelation, HBaseTableCatalog}
import org.apache.spark.sql.{DataFrame, SparkSession}

object ShcApp {


  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("bulkload").setMaster("local[*]")

    val tableName = "test"

    val spark = SparkSession.builder.appName("Spark Hbase").master("local[*]").getOrCreate()

    import spark.implicits._
    val sc = spark.sparkContext

    val df: DataFrame = spark.sqlContext.read.options(Map(HBaseTableCatalog.tableCatalog -> Catalog.errcrSchema, HBaseRelation.MIN_STAMP -> "1541921214000", // HbaseRdd 设置 case (q:Scan, None, Some(minStamp), Some(maxStamp)) => q.setTimeRange(minStamp, maxStamp)
      HBaseRelation.MAX_STAMP -> "1552007614000")).format("org.apache.spark.sql.execution.datasources.hbase").load()

    df.cache()
    df.createOrReplaceTempView("test")
    df.printSchema()
    val df2: DataFrame = spark.sql(s"select * from ${tableName}")

    df2.as[errorr].write.csv("file:///as.csv")

    spark.close()

  }
}

