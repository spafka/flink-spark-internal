package com.github.spafka.rpc

import java.util
import java.util.concurrent.CompletableFuture

import akka.actor.{ActorIdentity, ActorRef, ActorSystem, Address, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.github.spafka.util.{AkkaUtils, Logging}
import javax.annotation.concurrent.GuardedBy
import org.apache.flink.api.common.time.Time

import scala.util.{Failure, Success}

class AkkaRpcService(val actorSystem: ActorSystem,
                     val timeout: Time = Time.seconds(1L))
    extends RpcService
    with Logging {

  import java.lang.reflect.{InvocationHandler, Proxy}
  import java.util.function.Function

  import akka.actor.Identify

  import scala.concurrent.ExecutionContextExecutor

  val address: Address = AkkaUtils.getAddress(actorSystem)

  implicit val sc: ExecutionContextExecutor = actorSystem.dispatcher

  @GuardedBy("lock") private val actors =
    new util.HashMap[ActorRef, RpcEndpoint](4)

  override def startServer[C <: RpcEndpoint with RpcGateway](
    rpcEndpoint: C
  ): RpcServer = {
    import java.util

    import com.github.spafka.util.RpcUtils

    log.info(s"Starting Rpc Server")

    require(rpcEndpoint != null, "rpc endpoint")

    // Akka ActorSysterm 构造RpcEndPoint
    var akkaRpcActorProps = Props.create(
      classOf[AkkaRpcActor[_ <: RpcEndpoint with RpcGateway]],
      rpcEndpoint
    )

    // AkkaRpcActor actor实例
    var actorRef =
      actorSystem.actorOf(akkaRpcActorProps, rpcEndpoint.endpointId)
    actors.put(actorRef, rpcEndpoint)

    logInfo(
      s"Starting RPC endpoint for ${rpcEndpoint.getClass.getName} at ${actorRef.path} ."
    )

    val akkaAddress: String = AkkaUtils.getAkkaURL(actorSystem, actorRef)
    var hostname: String = null
    val host: Option[String] = actorRef.path.address.host
    if (host.isEmpty) hostname = "localhost" else hostname = host.get

    val implementedRpcGateways: util.Set[Class[_]] = new util.HashSet[Class[_]](
      RpcUtils.extractImplementedRpcGateways(rpcEndpoint.getClass)
    )

    implementedRpcGateways.add(classOf[RpcServer])

    var akkaInvocationHandler: InvocationHandler = null

    akkaInvocationHandler =
      new AkkaInvocationHandler(akkaAddress, hostname, actorRef, false, timeout)

    val classLoader: ClassLoader = getClass.getClassLoader

    import scala.collection.JavaConverters._
    val server: RpcServer = Proxy
      .newProxyInstance(
        classLoader, //
        implementedRpcGateways.asScala.toArray, //
        akkaInvocationHandler
      ) //
      .asInstanceOf[RpcServer]

    return server
  }

  override def getAddress: String =
    AkkaUtils.getAddress(actorSystem).host.getOrElse("")

  override def getPort: Int = AkkaUtils.getAddress(actorSystem).port.get

  override def connect[T <: RpcGateway](x: String,
                                        clazz: Class[T]): CompletableFuture[T] =
    connectInternal(x, clazz, (actorRef: ActorRef) => {

      new AkkaInvocationHandler(
        x,
        hostname = x,
        actorRef,
        false,
        Time.seconds(5)
      )
    })

  private def connectInternal[C <: RpcGateway](
    address: String,
    clazz: Class[C],
    invocationHandlerFactory: Function[ActorRef, InvocationHandler]
  ) = {
    import java.util.concurrent.TimeUnit

    logInfo(
      s"Try to connect to remote RPC endpoint with address ${address}. Returning a ${clazz.getName} gateway.",
    )

    val actorSel = actorSystem.actorSelection(address)

    val identifyFuture = new CompletableFuture[ActorIdentity]

    implicit val timeout = Timeout(100, TimeUnit.SECONDS)

    val future = actorSel ? new Identify(42)
    future.mapTo[ActorIdentity] onComplete {
      case Success(x) => {
        identifyFuture.complete(x)
      }
      case Failure(e) => {
        identifyFuture.completeExceptionally(e)
      }
    }

    val proxy: CompletableFuture[C] = identifyFuture.thenApply(x ⇒ {
      val invocationHandler = invocationHandlerFactory.apply(x.getRef)

      val classLoader = getClass.getClassLoader

      @SuppressWarnings(Array("unchecked")) val proxy: C = Proxy
        .newProxyInstance(
          classLoader,
          Array[Class[_]](clazz),
          invocationHandler
        )
        .asInstanceOf[C]
      proxy
    })

    proxy
  }

}

object AkkaRpcService {
  implicit val timeOut: Time ⇒ Timeout = (time: Time) ⇒
    Timeout(time.getSize, time.getUnit)
}
