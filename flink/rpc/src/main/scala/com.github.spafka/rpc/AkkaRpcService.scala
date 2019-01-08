package com.github.spafka.rpc

import java.util
import java.util.concurrent.{Callable, CompletableFuture}

import akka.actor.{ActorRef, ActorSystem, Address, Props}
import com.github.spafka.util.{AkkaUtils, Logging}
import javax.annotation.concurrent.GuardedBy
import org.apache.flink.api.common.time.Time


class AkkaRpcService(val actorSystem: ActorSystem, val timeout: Time = Time.seconds(1L)) extends RpcService with Logging {

  val address: Address = AkkaUtils.getAddress(actorSystem)

  @GuardedBy("lock") private val actors = new util.HashMap[ActorRef, RpcEndpoint](4)

  override def startServer[C <: RpcEndpoint with RpcGateway](rpcEndpoint: C): RpcServer = {

    log.info(s"starting Rpc Server") //fixme
    // fixme use akka
    val actorRef = actorSystem.actorOf(Props(classOf[AkkaRpcActor[C]], rpcEndpoint), rpcEndpoint.endpointId)

    null
  }

  override def getAddress: String = ???

  override def getPort: Int = ???

  override def start: Unit = ???

  override def stop: Unit = ???

  override def preStart: Unit = ???

  override def preStop: Unit = ???

  override def connect[T <: RpcGateway](adress: String): CompletableFuture[T] = ???

  override def execute(runnable: Runnable): Unit = ???

  override def execute[T](callable: Callable[T]): CompletableFuture[T] = ???
}
