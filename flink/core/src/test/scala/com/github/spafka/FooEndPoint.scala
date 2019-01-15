package com.github.spafka
import com.github.spafka.rpc.{RpcEndpoint, RpcService}

trait FooGateWay {

  def foo: String
}

trait BarGateWay {

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

  new AkkaRpcService(actorSystem)

  new FooEndPoint(new AkkaRpcService(actorSystem), "fooEndPoint")

}

object BarEndPoint extends App {
  import java.util.concurrent.CompletableFuture

  import akka.actor.ActorSystem
  import com.github.spafka.rpc.AkkaRpcService
  import com.github.spafka.util.AkkaUtils

  private val actorSystem: ActorSystem = AkkaUtils.startSlaveActorSystem()

  private val value: CompletableFuture[FooEndPoint] = new AkkaRpcService(
    actorSystem
  ).connect("akka://flink/user/fooEndPoint", classOf[FooEndPoint])

  private val point: FooEndPoint = value.get()

  point.foo
}
