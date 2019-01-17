package com.github.spafka
import java.util.concurrent.TimeUnit

import com.github.spafka.rpc.{RpcEndpoint, RpcGateway, RpcService}

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

  override def foo = {

    println("foo")
    "foo"
  }
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

  import akka.actor.ActorSystem
  import com.github.spafka.rpc.AkkaRpcService
  import com.github.spafka.util.AkkaUtils

  private val actorSystem2: ActorSystem = AkkaUtils.startSlaveActorSystem()

  val address = "akka.tcp://flink@127.0.0.1:6332/user/fooEndPoint";

  val fooGateWay: CompletableFuture[FooGateWay] =
    new AkkaRpcService(actorSystem2)
      .connect(address, classOf[FooGateWay])
  private val foo: String = fooGateWay.get().foo

  println(foo)
  TimeUnit.SECONDS.sleep(100)

}
