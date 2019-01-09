package com.github.spafka.rpc

import java.util
import java.util.concurrent.{Callable, CompletableFuture}

import akka.actor.{ActorRef, ActorSystem, Address, Props}
import com.github.spafka.util.{AkkaUtils, Logging}
import javax.annotation.concurrent.GuardedBy
import org.apache.flink.api.common.time.Time


class AkkaRpcService(val actorSystem: ActorSystem, val timeout: Time = Time.seconds(1L)) extends RpcService with Logging {

  import java.lang.reflect.{InvocationHandler, Proxy}

  val address: Address = AkkaUtils.getAddress(actorSystem)

  @GuardedBy("lock") private val actors = new util.HashMap[ActorRef, RpcEndpoint](4)

  override def startServer[C <: RpcEndpoint with RpcGateway](rpcEndpoint: C): RpcServer = {
    import java.util

    import com.github.spafka.util.RpcUtils

    log.info(s"starting Rpc Server")

    require(rpcEndpoint != null, "rpc endpoint")

    var akkaRpcActorProps = Props.create(classOf[AkkaRpcActor[_ <: RpcEndpoint with RpcGateway]], rpcEndpoint)

    var actorRef = actorSystem.actorOf(akkaRpcActorProps, rpcEndpoint.endpointId)
    actors.put(actorRef, rpcEndpoint)


    log.info(s"Starting RPC endpoint for ${rpcEndpoint.getClass.getName} at ${actorRef.path} .")

    val akkaAddress: String = AkkaUtils.getAkkaURL(actorSystem, actorRef)
    var hostname: String = null
    val host: Option[String] = actorRef.path.address.host
    if (host.isEmpty) hostname = "localhost" else hostname = host.get

    val implementedRpcGateways: util.Set[Class[_]] = new util.HashSet[Class[_]](RpcUtils.extractImplementedRpcGateways(rpcEndpoint.getClass))

    implementedRpcGateways.add(classOf[RpcServer])

    var akkaInvocationHandler: InvocationHandler = null

    akkaInvocationHandler = new AkkaInvocationHandler(akkaAddress, hostname, actorRef, timeout = Time.seconds(1L))


    val classLoader: ClassLoader = getClass.getClassLoader

    import scala.collection.JavaConverters._
    val server: RpcServer = Proxy.newProxyInstance(classLoader, //
      implementedRpcGateways.asScala.toArray, //
      akkaInvocationHandler) //
      .asInstanceOf[RpcServer]

    return server
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
