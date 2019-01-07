package com.github.spafka

import com.github.spafka.util.AkkaUtils
import org.junit.Test

class rpcTest {

  @Test def test(): Unit = {

    val actorSystem = AkkaUtils.startDebugActorSystem()

    println(actorSystem)
  }
}
