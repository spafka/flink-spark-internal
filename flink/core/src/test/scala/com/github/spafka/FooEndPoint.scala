package com.github.spafka
import java.util.concurrent.TimeUnit

import akka.actor.{ActorIdentity, Identify}
import akka.util.Timeout
import com.github.spafka.rpc.{RpcEndpoint, RpcGateway, RpcService}

import scala.util.{Failure, Success}

trait FooGateWay extends RpcGateway {

  def foo: String
}

trait BarGateWay extends RpcGateway {

  def bar: String
}

class FooEndPoint(rpcService: RpcService, endpointId: String)
    extends RpcEndpoint(rpcService: RpcService, endpointId: String)
    with FooGateWay {

  override def start: Unit = { rpcServer.start }
  override def stop: Unit = { rpcServer.stop }
  override def getAddress: String = rpcService.getAddress
  override def getHostname: String = rpcService.getAddress

  override def foo: String = { "foo" }
}

class BarEndPoint(rpcService: RpcService, endpointId: String)
    extends RpcEndpoint(rpcService: RpcService, endpointId: String)
    with BarGateWay {

  override def start: Unit = { rpcServer.start }
  override def stop: Unit = { rpcServer.stop }
  override def getAddress: String = rpcService.getAddress
  override def getHostname: String = rpcService.getAddress

  override def bar: String = { "bar" }
}

object FooEndPoint extends App {
  import akka.actor.ActorSystem
  import com.github.spafka.rpc.AkkaRpcService
  import com.github.spafka.util.AkkaUtils

  private val actorSystem: ActorSystem = AkkaUtils.startMasterActorSystem()

  new FooEndPoint(new AkkaRpcService(actorSystem), "fooEndPoint")

}

object BarEndPoint extends App {
  import java.util.concurrent.CompletableFuture

  import akka.actor.{ActorSelection, ActorSystem}
  import com.github.spafka.rpc.AkkaRpcService
  import com.github.spafka.util.AkkaUtils
  import akka.pattern.ask

  private val actorSystem2: ActorSystem = AkkaUtils.startSlaveActorSystem()

  val address = "akka.tcp://flink@127.0.0.1:6332/user/fooEndPoint";

//  val actorSel = actorSystem2.actorSelection(address)
//
//  val identifyFuture = new CompletableFuture[ActorIdentity]
//
//  implicit val timeout = Timeout(10, TimeUnit.SECONDS)
//  implicit val sc = actorSystem2.dispatcher
//  val future = actorSel ? new Identify(42)
//  future.mapTo[ActorIdentity] onComplete {
//    case Success(x) => {
//      identifyFuture.complete(x)
//    }
//    case Failure(e) => {
//      identifyFuture.completeExceptionally(e)
//    }
//  }

  private val value: CompletableFuture[BarGateWay] =
    new AkkaRpcService(actorSystem2)
      .connect(address, classOf[BarGateWay])

  private val point: BarGateWay = value.get()
  point.bar

  TimeUnit.SECONDS.sleep(100)

}
