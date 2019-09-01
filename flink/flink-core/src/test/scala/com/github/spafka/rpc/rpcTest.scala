package com.github.spafka.rpc

import com.github.spafka.rpc.akka.AkkaRpcService
import com.github.spafka.util.{AkkaUtils, Logging}
import org.apache.flink.api.common.time.Time
import org.junit.{Before, Test}

class rpcTest {

  var actorSystem: ActorSystem = _
  val time = Time.seconds(1)

  @Before def initactorSysterm(): Unit = {
    actorSystem = AkkaUtils.startMasterActorSystem()
  }

  @Test def test(): Unit = {

    // 先建立actorSysterm
    val rpcService = new AkkaRpcService(actorSystem, time)

    val taskExecutor = new TaskExecutor(rpcService, "task")

    taskExecutor.start

  }
}

trait TaskGateWay extends RpcGateway {

  def regiest = {}

}

class TaskExecutor(rpcService: RpcService, endpointId: String)
  extends RpcEndpoint(rpcService, endpointId)
    with TaskGateWay
    with Logging {
  override def start: Unit = {

    // rpcService.connect("")
  }

  override def stop: Unit = ???

  //  override def preStart: Unit = ???
  //
  //  override def preStop: Unit = ???

  override def getAddress: String = ???

  override def getHostname: String = ???
}

trait JobGateWay extends RpcGateway {}
