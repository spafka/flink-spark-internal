package com.github.spafka.rpc

import java.lang.reflect.{InvocationHandler, Method}

import akka.actor.ActorRef
import com.github.spafka.message.RemoteRpcInvocation
import com.github.spafka.util.Logging


// 动态代理，使之调Rpc就像调用本地一样
class AkkaInvocationHandler(rpcendPoint: ActorRef) extends InvocationHandler with RpcServer with Logging {

  override def invoke(o: Any, method: Method, args: Array[AnyRef]): AnyRef = {

    val methodName = method.getName
    val parameterTypes = method.getParameterTypes
    val parameterAnnotations = method.getParameterAnnotations
    val remoteRpcInvocation = new RemoteRpcInvocation(methodName, parameterTypes, args)


    rpcendPoint ! remoteRpcInvocation
    return null

  }
}
