package com.github.spafka.rpc

import java.util
import java.util.concurrent.{Callable, CompletableFuture}

import akka.actor.{ActorRef, ActorSystem, Address, Props}
import com.github.spafka.util.{AkkaUtils, Logging}
import javax.annotation.concurrent.GuardedBy
import org.apache.flink.api.common.time.Time

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

    akkaInvocationHandler = new AkkaInvocationHandler(
      akkaAddress,
      hostname,
      actorRef,
      timeout = Time.seconds(1L)
    )

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

  override def getAddress: String = ???

  override def getPort: Int = ???

  override def start: Unit = ???

  override def stop: Unit = ???

  override def preStart: Unit = ???

  override def preStop: Unit = ???

  override def connect[T <: RpcGateway](adress: String,
                                        clazz: Class[T]): CompletableFuture[T] =
    connectInternal(address.host.get, clazz, (actorRef: ActorRef) => {

      new AkkaInvocationHandler(
        adress,
        hostname = adress,
        actorRef,
        false,
        Time.seconds(5)
      )
    })

  override def execute(runnable: Runnable): Unit = ???
  override def execute[T](callable: Callable[T]): CompletableFuture[T] = ???

  private def connectInternal[C <: RpcGateway](
    address: String,
    clazz: Class[C],
    invocationHandlerFactory: Function[ActorRef, InvocationHandler]
  ) = {

    logInfo(
      s"Try to connect to remote RPC endpoint with address ${address}. Returning a ${clazz.getName} gateway.",
    )

    val actorSel = actorSystem.actorSelection(address)

    import akka.actor.ActorIdentity
    import akka.pattern.ask
    import akka.util.Timeout

    import scala.concurrent.duration._

    implicit val timeout = Timeout(5 seconds)

    val identifyFuture = new CompletableFuture[ActorIdentity]
    import scala.util.{Failure, Success}
    val future = actorSel ? new Identify(42)
    future onComplete {
      case Success(x) => {
        identifyFuture.complete(x.asInstanceOf[ActorIdentity])
      }
      case Failure(e) => { identifyFuture.completeExceptionally(e) }
    }

    val proxy: CompletableFuture[C] = identifyFuture.thenApply(x ⇒ {
      val invocationHandler = invocationHandlerFactory.apply(x.getRef)

      val classLoader = getClass.getClassLoader

      @SuppressWarnings(Array("unchecked")) val proxy = Proxy
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
