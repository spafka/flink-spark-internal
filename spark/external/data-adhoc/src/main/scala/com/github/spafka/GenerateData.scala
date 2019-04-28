package com.github.spafka

import com.github.spafka.shc.errorr
import org.apache.spark.sql.{Dataset, SparkSession}

object GenerateData {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("thrift").master("local[*]").enableHiveSupport().getOrCreate()

    val a = 1 << 25
    import spark.implicits._
    import spark.sql
    //    val ds: Dataset[errorr] = spark.range(1, a ).map(x => errorr.apply(x.toString)).as[errorr]
    //    ds.write.parquet("d100")
    spark.read.parquet("d100").as[errorr].createOrReplaceTempView("d100")
    sql("select col1,count(*)  as c from d100 group by col1 order by  c desc").show()

    sql("select count(*) from d100").show()
    spark.stop()

  }
}
