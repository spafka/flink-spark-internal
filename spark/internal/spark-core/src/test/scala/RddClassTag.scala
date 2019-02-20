import org.junit.Test
import spark.{MappedRDD, ParallelArray, SparkContext}

class RddClassTag {

  @Test
  def rdd1 = {


    val spark = new SparkContext("local", "SparkPi")
    val slices = 2

    val rdd1: ParallelArray[Int] = spark.parallelize(Seq(1,2,3,4),1)
    val rdd2: MappedRDD[String, Int] = rdd1.map(x=>x.toString)

    val collect = rdd2.toArray()

    collect.foreach(println(_))

  }
}
