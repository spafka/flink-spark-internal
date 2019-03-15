package org.spafka.spark

object SparkSubmit2 {

  def main(args: Array[String]): Unit = {
    import org.apache.spark.deploy.SparkSubmit
    val strings: Array[String] = ("--master=spark://0.0.0.0:7077\n" +
      "--class=org.apache.spark.examples.SparkPi\n" +
      "--verbose=true\n" +
      "mini-cluster/spark-server/src/main/assembly/original-spark-examples_2.12-3.0.0-SNAPSHOT.jar\n")
      .split("\n")
    SparkSubmit.main(strings);
  }
}
