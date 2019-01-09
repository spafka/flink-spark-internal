package com.github.spafka.rpc

/**
  *
  * @Author github.com/spafka
  * @Date 2019/1/6
  *       rpc网关,提供基础功能，必须要有网关地址
  */
trait RpcGateway {

  def getAddress: String

  def getHostname: String
}
