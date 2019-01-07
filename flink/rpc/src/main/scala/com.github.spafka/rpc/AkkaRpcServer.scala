package com.github.spafka.rpc

import java.lang.reflect.{InvocationHandler, Method}

import akka.actor.ActorSystem

class AkkaRpcServer(val actorSystem: ActorSystem, val timeout: Long = 1000) extends RpcServer with InvocationHandler {
  override def invoke(o: Any, method: Method, objects: Array[AnyRef]): AnyRef = {

    actorSystem.actorSelection("")
  }
}
