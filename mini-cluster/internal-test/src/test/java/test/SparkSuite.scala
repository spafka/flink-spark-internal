package test

import io.github.spafka.spark.util.Utils
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.{Before, Test}

class SparkSuite {

  var sc: SparkContext = null

  @Before
  def init = {

    val conf = new SparkConf()
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .setAppName(s"TeraSort")
      .setMaster("local[*]")
    sc = new SparkContext(conf)


  }


  @Test
  def count(): Unit = {

    println(Utils.timeTakenMs {
      sc.textFile("random.txt").count()
    })
  }

}
