package com.github.spafka.rpc

import java.lang.reflect.{InvocationHandler, Method}

import akka.actor.ActorRef
import com.github.spafka.message.RemoteRpcInvocation
import com.github.spafka.util.Logging
import org.apache.flink.api.common.time.Time


// 动态代理，使之调Rpc就像调用本地一样
class AkkaInvocationHandler(var address: String = null, //
                            var hostname: String = null, //
                            var actorRef: ActorRef = null, //
                            var isLocal: Boolean = false, //
                            // default timeout for asks
                            var timeout: Time = null) extends InvocationHandler with RpcServer with Logging {

  import java.util.concurrent.CompletableFuture

  override def invoke(o: Any, method: Method, args: Array[AnyRef]): AnyRef = {

    val methodName = method.getName
    val parameterTypes = method.getParameterTypes
    val parameterAnnotations = method.getParameterAnnotations
    val remoteRpcInvocation = new RemoteRpcInvocation(methodName, parameterTypes, args)


    // fixme return some
    //    rpcendPoint ! remoteRpcInvocation
    return null

  }

  override def getTerminationFuture: CompletableFuture[Void] = ???
}
