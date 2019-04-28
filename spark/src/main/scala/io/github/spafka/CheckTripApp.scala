package io.github.spafka

object CheckTripApp {


  def main(args: Array[String]): Unit = {
    import org.apache.spark.sql.SparkSession


    val spark = SparkSession.builder().master("local[*]").appName("trip").getOrCreate()

    import spark.implicits._
    import org.apache.spark.sql.functions._
    spark.read.json("spark/src/main/resources/call2").createOrReplaceTempView("call")

    spark.sql("select * from call").show

    val sealt = spark.read.json("spark/src/main/resources/sealt")

    sealt.printSchema()
    sealt.createOrReplaceTempView("sealt")

    spark.sql("select a.accOn,a.deviceId,from_unixtime(a.seconds1970) as time ,b.driverSeatBelt from call a inner join sealt b on a.seconds1970 = b.seconds1970 order by a.seconds1970").show(10000)


    spark.stop()


  }
}
