package com.github.spafka

import org.junit.Test

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
class java_concurrent {

  // 对共享变量的并发访问会导致多线程问题
  @Test
  def use_share_variable() = {
    var i, j = 0
    (1 to 100000).foreach(_ => Future{i = i + 1})
    (1 to 100000).foreach(_ => j = j + 1)
    Thread.sleep(1000)
    println(s"${i} ${j}")
  }
}
