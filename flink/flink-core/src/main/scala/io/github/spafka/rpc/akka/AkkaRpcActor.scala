package io.github.spafka.rpc.akka

import akka.actor.Actor
import com.github.spafka.rpc.message.RpcInvocation
import com.github.spafka.rpc.{RpcEndpoint, RpcGateway}
import com.github.spafka.util.Logging
import io.github.spafka.rpc.message.RpcInvocation
import io.github.spafka.rpc.{RpcEndpoint, RpcGateway}
import io.github.spafka.util.Logging

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
