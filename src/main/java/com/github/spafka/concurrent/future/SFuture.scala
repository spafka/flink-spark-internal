package com.github.spafka.concurrent.future

import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SFuture {

  //  // 自定义的forkJoinpool
  //  implicit lazy val workStealingPoolExecutionContext: ExecutionContext = {
  //    val workStealingPool: ExecutorService = Executors.newWorkStealingPool
  //    val executor = ExecutionContext.fromExecutor(workStealingPool)
  //    executor
  //  }

  @Test
  def useScalaFuture = {
    Future {
      Thread.sleep(1)
      1
    } onComplete {
      case scala.util.Success(value) => {
        print(value)
      }
      case scala.util.Failure(exception) => {}
    }

    Thread.sleep(1000)
  }
}
