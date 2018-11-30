package com.github.spafka.rpc

import akka.actor.Actor
import com.github.spafka.message.Message

class FlinkRpcServer(actor: Actor) extends FlinkGateWay {


  override def getAddress: String = {
    ""
  }

  override def getHostname: String = {
    ""
  }

  override def invokerRpc(msg: Message): Unit = {

  }
}
