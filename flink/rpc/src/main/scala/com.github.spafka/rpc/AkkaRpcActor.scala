package com.github.spafka.rpc

import akka.actor.Actor
import com.github.spafka.util.Logging
import com.github.spafka.message.RpcInvocation

class AkkaRpcActor[T <: RpcEndpoint with RpcGateway](val rpcEndpoint: T)
    extends Actor
    with Logging {

  override def receive: Receive = {
    case rpcInvocation: RpcInvocation ⇒ handleRpcInvocation(rpcInvocation)
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
