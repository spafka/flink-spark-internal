package com.github.spafka.rpc

trait RpcGateWay {

  def getAddress: String

  def getHostname: String
}
