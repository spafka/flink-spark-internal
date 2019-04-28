/**
  * test spark rdd operation
  */
class SparkJTest {

  import org.apache.spark.SparkContext
  import org.apache.spark.sql.SparkSession
  import org.junit.{Before, Test}

  var sparkSession: SparkSession = _
  var sc: SparkContext = _

  @Before def init = {
    import org.apache.spark.sql.SparkSession
    sparkSession = SparkSession.builder().master("local[*]").appName("junit").getOrCreate()
    sc = sparkSession.sparkContext

  }

  @Test def _1(): Unit = {

    sparkSession.sql("show tables").show()

  }

  @Test def foreach(): Unit = {
    sc.textFile("pom.xml").foreach(x ⇒ println(x))
  }

  @Test def wordcount() = {
    import org.apache.spark.rdd.RDD


    sc.textFile("pom.xml").flatMap(x ⇒ x.split(" ")).map(x ⇒ (x, 1)).reduceByKey(_ + _).foreach(x ⇒ println(x))


  }
}
