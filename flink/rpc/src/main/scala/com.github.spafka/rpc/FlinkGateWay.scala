package com.github.spafka.rpc

import com.github.spafka.message.Message

trait FlinkGateWay extends RpcGateWay{

  def invokerRpc(msg:Message):Unit


}
