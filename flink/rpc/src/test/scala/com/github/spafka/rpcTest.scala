package com.github.spafka

import akka.actor.ActorSystem
import com.github.spafka.rpc.{AkkaRpcService, RpcEndpoint}
import com.github.spafka.util.AkkaUtils
import org.apache.flink.api.common.time.Time
import org.junit.{Before, Test}

class rpcTest {

  var actorSystem: ActorSystem = _
  val time = Time.seconds(1)

  @Before def initactorSysterm(): Unit = {
    actorSystem = AkkaUtils.startMasterActorSystem()
  }


  @Test def test(): Unit = {

    val service = new AkkaRpcService(actorSystem, time)

    new RpcEndpoint(service, "test") {
      override def start: Unit = {
        rpcService.start
      }

      override def stop: Unit = ???

      override def preStart: Unit = ???

      override def preStop: Unit = ???

      override def getAddress: String = ???

      override def getHostname: String = ???
    }


  }
}
