package io.github.spafka.rpc

import java.util.function.{Function => JFunction}

import io.github.spafka.util.Logging
import org.junit.Test

class JFuture extends Logging {

  import java.util.concurrent.Executors

  val ex = Executors.newCachedThreadPool()

  @Test def testFuture(): Unit = {
    import java.util.concurrent.{Callable, CompletableFuture, TimeUnit}

    ex.submit(new Callable[Int]() {
      override def call(): Int = 1
    })

    val stringToString: JFunction[_ >: String, _ <: String] = (a: String) ⇒
      a + "c"
    val value: CompletableFuture[String] = CompletableFuture
      .supplyAsync[String](() ⇒ {
      "a"
    })
      .thenApplyAsync[String](((t: String) ⇒ {
      t + "b"
    }): JFunction[_ >: String, _ <: String]) //
      .thenApplyAsync[String](((t: String) ⇒ {
      t + "c"
    }): JFunction[_ >: String, _ <: String]) //
    println(value.get())
    TimeUnit.SECONDS.sleep(1)
  }

}
