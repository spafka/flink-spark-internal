package com.github.spafka.message


import java.io.IOException


trait RpcInvocation {

  @throws[IOException]
  @throws[ClassNotFoundException] def getMethodName: String

  @throws[IOException]
  @throws[ClassNotFoundException] def getParameterTypes: Array[Class[_]]

  @throws[IOException]
  @throws[ClassNotFoundException] def getArgs: Array[AnyRef]
}


@SerialVersionUID(9187962608946082519L) case class RemoteRpcInvocation(var methodName: String, val parameterTypes: Array[Class[_]], var args: Array[AnyRef]) extends Serializable {

  private[messages] def getMethodName = methodName

  private[messages] def getParameterTypes = parameterTypes

  private[messages] def getArgs = args

}

