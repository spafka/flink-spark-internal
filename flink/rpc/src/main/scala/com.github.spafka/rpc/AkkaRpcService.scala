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

  val address: Address = AkkaUtils.getAddress(actorSystem)

  @GuardedBy("lock") private val actors =
    new util.HashMap[ActorRef, RpcEndpoint](4)

  override def startServer[C <: RpcEndpoint with RpcGateway](
      rpcEndpoint: C): RpcServer = {
    import java.util

    import com.github.spafka.util.RpcUtils

    log.info(s"Starting Rpc Server")

    require(rpcEndpoint != null, "rpc endpoint")

    // Akka ActorSysterm 构造RpcEndPoint
    var akkaRpcActorProps = Props.create(
      classOf[AkkaRpcActor[_ <: RpcEndpoint with RpcGateway]],
      rpcEndpoint)

    // AkkaRpcActor actor实例
    var actorRef =
      actorSystem.actorOf(akkaRpcActorProps, rpcEndpoint.endpointId)
    actors.put(actorRef, rpcEndpoint)

    logInfo(
      s"Starting RPC endpoint for ${rpcEndpoint.getClass.getName} at ${actorRef.path} .")

    val akkaAddress: String = AkkaUtils.getAkkaURL(actorSystem, actorRef)
    var hostname: String = null
    val host: Option[String] = actorRef.path.address.host
    if (host.isEmpty) hostname = "localhost" else hostname = host.get

    val implementedRpcGateways: util.Set[Class[_]] = new util.HashSet[Class[_]](
      RpcUtils.extractImplementedRpcGateways(rpcEndpoint.getClass))

    implementedRpcGateways.add(classOf[RpcServer])

    var akkaInvocationHandler: InvocationHandler = null

    akkaInvocationHandler = new AkkaInvocationHandler(
      akkaAddress,
      hostname,
      actorRef,
      timeout = Time.seconds(1L))

    val classLoader: ClassLoader = getClass.getClassLoader

    import scala.collection.JavaConverters._
    val server: RpcServer = Proxy
      .newProxyInstance(classLoader, //
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

  override def connect[T <: RpcGateway](
      adress: String): CompletableFuture[T] = {}

  override def execute(runnable: Runnable): Unit = ???

  override def execute[T](callable: Callable[T]): CompletableFuture[T] = ???

  private def connectInternal[C <: RpcGateway](
      address: String,
      clazz: Class[C],
      invocationHandlerFactory: Function[ActorRef, InvocationHandler]) = {
    import java.util.concurrent.Future

    import akka.actor.{ActorIdentity, ActorSelection}
    import akka.pattern.Patterns

    logInfo(
      s"Try to connect to remote RPC endpoint with address ${address}. Returning a ${clazz.getName} gateway.",
    )

    val actorSel: ActorSelection = actorSystem.actorSelection(address)

    val identify: Future[ActorIdentity] = Patterns
      .ask(actorSel, new Identify(42), timeout.toMilliseconds)
      .mapTo[ActorIdentity](
        ClassTag$.MODULE$.apply[ActorIdentity](classOf[ActorIdentity])
      )

    val identifyFuture: CompletableFuture[ActorIdentity] =
      FutureUtils.toJava(identify)

    val actorRefFuture: CompletableFuture[ActorRef] =
      identifyFuture.thenApply((actorIdentity: ActorIdentity) => {
        def foo(actorIdentity: ActorIdentity) = {
          if (actorIdentity.getRef == null) {
            import java.util.concurrent.CompletionException
            throw new CompletionException(
              new RpcConnectionException(
                "Could not connect to rpc endpoint under address " + address + '.'
              )
            )
          } else { return actorIdentity.getRef }
        }
        foo(actorIdentity)
      })

    val handshakeFuture: CompletableFuture[HandshakeSuccessMessage] =
      actorRefFuture.thenCompose(
        (actorRef: ActorRef) =>
          FutureUtils.toJava(
            Patterns
              .ask(
                actorRef,
                new RemoteHandshakeMessage(clazz, getVersion),
                timeout.toMilliseconds
              )
              .mapTo[HandshakeSuccessMessage](
                ClassTag$.MODULE$
                  .apply[HandshakeSuccessMessage](
                    classOf[HandshakeSuccessMessage])
              )
        )
      )

    return actorRefFuture.thenCombineAsync(
      handshakeFuture,
      (actorRef: ActorRef, ignored: HandshakeSuccessMessage) => {
        def foo(actorRef: ActorRef, ignored: HandshakeSuccessMessage) = {
          val invocationHandler: InvocationHandler =
            invocationHandlerFactory.apply(actorRef)
// Rather than using the System ClassLoader directly, we derive the ClassLoader
// from this class . That works better in cases where Flink runs embedded and all Flink
// code is loaded dynamically (for example from an OSGI bundle) through a custom ClassLoader
          val classLoader: ClassLoader = getClass.getClassLoader
          @SuppressWarnings(Array("unchecked")) val proxy: C = Proxy
            .newProxyInstance(
              classLoader,
              Array[Class[_]](clazz),
              invocationHandler
            )
            .asInstanceOf[C]
          return proxy
        }
        foo(actorRef, ignored)
      },
      actorSystem.dispatcher
    )
  }
}
