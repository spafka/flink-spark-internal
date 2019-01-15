package com.github.spafka.rpc

import akka.actor.Actor
import com.github.spafka.util.Logging
import com.github.spafka.message.RpcInvocation

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
  }

  @throws[NoSuchMethodException]
  private def lookupRpcMethod(methodName: String,
                              parameterTypes: Array[Class[_]]) =
    rpcEndpoint.getClass.getMethod(methodName, parameterTypes: _*)

  private def handleRpcInvocation(rpcInvocation: RpcInvocation) = {

    val methodName = rpcInvocation.getMethodName
    val parameterTypes = rpcInvocation.getParameterTypes
    val rpcMethod = lookupRpcMethod(methodName, parameterTypes)

    val value = rpcMethod.invoke(rpcInvocation.getArgs)

    println(value)
  }

}
object State extends Enumeration {
  type State = Value
  val STARTED, STOPPED = Value
}
