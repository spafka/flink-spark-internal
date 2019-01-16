package com.github.spafka.rpc

import java.lang.reflect.{InvocationHandler, Method}

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.github.spafka.message.RemoteRpcInvocation
import com.github.spafka.util.Logging
import org.apache.flink.api.common.time.Time
import scala.concurrent.Future

// 动态代理，使之调Rpc就像调用本地一样
class AkkaInvocationHandler(var address: String = null, //
                            var hostname: String = null, //
                            val rpcEndpoint: ActorRef = null, //
                            var isLocal: Boolean = false, //
                            // default timeout for asks
                            var timeout: Time = Time.seconds(5L))
    extends InvocationHandler
    with RpcServer
    with Logging {

  import java.util.Objects
  import java.util.concurrent.{CompletableFuture, Executors}

  import scala.concurrent.ExecutionContext

  override def invoke(o: Any, method: Method, args: Array[AnyRef]): AnyRef = {

    val methodName = method.getName
    val parameterTypes = method.getParameterTypes
    val parameterAnnotations = method.getParameterAnnotations
    val remoteRpcInvocation =
      new RemoteRpcInvocation(methodName, parameterTypes, args)

    logInfo(
      s"AkkaInvocationHandler ${methodName} ${parameterTypes} ${remoteRpcInvocation}"
    )

    return invokeRpc(method, args, remoteRpcInvocation)

  }
  implicit val ec =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(1))
  @throws[Exception]
  private def invokeRpc(method: Method,
                        args: Array[AnyRef],
                        rpcInvocation: RemoteRpcInvocation): AnyRef = {
    val methodName = method.getName
    val parameterTypes = method.getParameterTypes
    val parameterAnnotations = method.getParameterAnnotations

    implicit val rpcTimeOut = Timeout(timeout.getSize, timeout.getUnit)
    val returnType = method.getReturnType
    var result: Any = null
    if (Objects.equals(returnType, Void.TYPE)) {
      rpcEndpoint ! (rpcInvocation)
      result = null
    } else if (Objects.equals(returnType, classOf[CompletableFuture[_]])) {
      val ff = new CompletableFuture[Any]()
      import scala.util.{Failure, Success}
      rpcEndpoint ? rpcInvocation onComplete {
        case Success(x) => { ff.complete(x) }
        case Failure(e) => { ff.completeExceptionally(e) }
      }
      result = ff
    } else {

      val f: Future[Any] = rpcEndpoint ? rpcInvocation

      f.onComplete {
        case scala.util.Success(value) ⇒ {
          result = value
        }
        case scala.util.Failure(exception) ⇒ throw exception
      }
    }
    result.asInstanceOf[AnyRef]
  }

  override def getAddress: String = { address }
  override def getHostname: String = { hostname }
  override def start: Unit = {}
  override def stop: Unit = {}
}
