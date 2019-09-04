package test

import io.github.spafka.spark.util.Utils
import org.apache.flink.api.scala.ExecutionEnvironment


object suite {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    println(Utils.timeTakenMs {
      env.readTextFile("D:\\OpenAi\\Apache\\flink-spark-internal\\mini-cluster\\internal-test\\random.txt").count()
    })


  }
}
