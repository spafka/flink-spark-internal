package com.github.spafka.rpc

/**
  *
  * @Author github.com/spafka
  * @Date 2019/1/6
  *
  */
abstract class RpcEndpoint(val rpcService: RpcService, val endpointId: String) extends RpcGateway {

  val rpcServer: RpcServer = rpcService.startServer(this)

  // lifecycle wirh rpcServer
  def start

  def stop

  def preStart

  def preStop

  // lifecycle end
}
