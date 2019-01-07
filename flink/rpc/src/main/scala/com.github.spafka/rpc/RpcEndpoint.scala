package com.github.spafka.rpc

/**
  *
  * @Author github.com/spafka
  * @Date 2019/1/6
  *
  */
private[rpc] trait RpcEndpoint extends RpcGateWay {

  @scala.beans.BeanProperty var rpcServer: RpcService = _

  // lifecycle wirh rpcServer
  def start

  def stop

  def preStart

  def preStop

  // lifecycle end
}
