import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import scala.math.random

object YarnClient {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf()
//      .setMaster("yarn")
//      .set("spark.submit.", "cluster")
//      .set("yarn.resourcemanager.hostname", "localhost")
//      .set("spark.executor.instances", "2")
//      .setJars(List("D:\\OpenAi\\Apache\\flink-spark-internal\\mini-cluster\\spark-server\\target\\spark-server-1.0.jar"))
//      //.set("spark.yarn.archive", "C:\\Users\\Administrator\\Downloads\\spark-2.4.3-bin-hadoop2\\spark-2.4.3-bin-hadoop2.6\\jars")
//      .set("spark.driver.host","192.168.88.113")

    val spark = SparkSession
      .builder
      .config(sparkConf)
      .appName("Spark Pi")
      .getOrCreate()
    val slices = if (args.length > 0) args(0).toInt else 2
    val n = math.min(100000L * slices, Int.MaxValue).toInt // avoid overflow
    val count = spark.sparkContext.parallelize(1 until n, slices).map { i =>
      val x = random * 2 - 1
      val y = random * 2 - 1
      if (x * x + y * y <= 1) 1 else 0
    }.reduce(_ + _)
    println(s"Pi is roughly ${4.0 * count / (n - 1)}")
    spark.stop()
  }
}
