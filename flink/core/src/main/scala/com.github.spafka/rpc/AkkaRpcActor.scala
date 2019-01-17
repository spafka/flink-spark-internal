package com.github.spafka.rpc

import akka.actor.Actor
import com.github.spafka.util.Logging
import com.github.spafka.rpc.message.RpcInvocation

class AkkaRpcActor[T <: RpcEndpoint with RpcGateway](val rpcEndpoint: T)
    extends Actor
    with Logging {
  var state = State.STOPPED

  override def receive: Receive = {
    case rpcInvocation: RpcInvocation ⇒ handleRpcInvocation(rpcInvocation)
    case State.STARTED ⇒ {
      logInfo(s"AkkaRpcActor starting")
      state = State.STARTED
    }
    case State.STARTED ⇒ {
      logInfo(s"AkkaRpcActor ending")
      state = State.STOPPED
    }
    case x ⇒ println(x)
  }

  @throws[NoSuchMethodException]
  private def lookupRpcMethod(methodName: String,
                              parameterTypes: Array[Class[_]]) =
    rpcEndpoint.getClass.getMethod(methodName, parameterTypes: _*)

  private def handleRpcInvocation(rpcInvocation: RpcInvocation) = {
    import java.util.concurrent.CompletableFuture

    val methodName = rpcInvocation.getMethodName
    val parameterTypes = rpcInvocation.getParameterTypes
    val rpcMethod = lookupRpcMethod(methodName, parameterTypes)

    rpcMethod.setAccessible(true)
    val result = rpcMethod.invoke(rpcEndpoint, rpcInvocation.getArgs: _*)

    sender() ! result

  }

}
object State extends Enumeration {
  type State = Value
  val STARTED, STOPPED = Value
}
