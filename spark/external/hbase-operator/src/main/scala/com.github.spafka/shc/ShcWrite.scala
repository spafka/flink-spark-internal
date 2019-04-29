package com.github.spafka.shc

object ShcWrite {

  def main(args: Array[String]): Unit = {
    import org.apache.spark.sql.SparkSession
    import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog

    val spark = SparkSession.builder().appName("WriteHBase").master("local[*]").getOrCreate()
    val sc = spark.sparkContext

    import spark.implicits._


    21 to 40 foreach (x => {
      val data = (1 to 10000).map(y => errorr.apply((x * 10000 + y).toString))

      // 写数据
      sc.parallelize(data).toDF.write.options(Map(HBaseTableCatalog.tableCatalog -> Catalog.errcrSchema, HBaseTableCatalog.newTable -> "5")).format("org.apache.spark.sql.execution.datasources.hbase").save()

      Thread.sleep(10000)
    })

    spark.stop()

  }

}
