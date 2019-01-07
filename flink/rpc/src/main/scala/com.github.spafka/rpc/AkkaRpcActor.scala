package com.github.spafka.rpc

import akka.actor.Actor
import com.github.spafka.util.Logging

class AkkaRpcActor[T <: RpcEndpoint with RpcGateway](val endPoint: T) extends Actor with Logging {

  override def receive: Receive = {
    case msg: String ⇒ println(s"$msg")
    case _ ⇒ println("other !!!!")

    // fixme proxy
  }
}
