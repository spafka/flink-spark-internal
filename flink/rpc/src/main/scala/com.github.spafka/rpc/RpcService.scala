package com.github.spafka.rpc

import java.util.concurrent.Callable

import scala.concurrent.Promise

/**
  *
  * @Author github.com/spafka
  * @Date 2019/1/6
  *       真正需要执行的rpc服务
  */
trait RpcService {

  // actual address
  def getAddress: String

  def getPort: Int

  // rpcService LifeCircle
  def start

  def stop

  def preStart

  def preStop

  // before rpc we muse conncect it
  def connect[T <: RpcGateWay](adress: String): Promise[T]

  def execute(runnable: Runnable): Unit

  def execute[T](callable: Callable[T]): Promise[T]


}
