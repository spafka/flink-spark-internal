package com.github.spafka.rpc

import java.util.concurrent.{Callable, CompletableFuture}

import scala.concurrent.Promise

// Rpc 基本抽象，可以获取本地地址，端口,启动一个bean Server,并拥有与远程RpcService 交互的能力
trait RpcService {

  // actual address
  def getAddress: String

  def getPort: Int

  // before rpc we muse conncect it
  def connect[T <: RpcGateway](adress: String,
                               clazz: Class[T]): CompletableFuture[T]

  //  def execute(runnable: Runnable): Unit
  //
  //  def execute[T](callable: Callable[T]): CompletableFuture[T]

  def startServer[C <: RpcEndpoint with RpcGateway](rpcEndpoint: C): RpcServer

}
