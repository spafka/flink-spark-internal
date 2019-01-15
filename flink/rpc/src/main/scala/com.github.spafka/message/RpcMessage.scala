package com.github.spafka.message

import java.io.IOException

trait Rpc

trait RpcInvocation extends Rpc {

  @throws[IOException]
  @throws[ClassNotFoundException]
  def getMethodName: String

  @throws[IOException]
  @throws[ClassNotFoundException]
  def getParameterTypes: Array[Class[_]]

  @throws[IOException]
  @throws[ClassNotFoundException]
  def getArgs: Array[AnyRef]
}
@SerialVersionUID(9187962608946082519L) case class RemoteRpcInvocation(
  var methodName: String,
  val parameterTypes: Array[Class[_]],
  var args: Array[AnyRef]
) extends Serializable {

  private def getMethodName = methodName

  private def getParameterTypes = parameterTypes

  private def getArgs = args

}

trait RpcMessage extends Rpc {}
