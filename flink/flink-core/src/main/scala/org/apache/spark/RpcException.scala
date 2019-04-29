package org.apache.spark

/**
  * Rpc Exception
  *
  * @param message
  * @param cause
  */
class RpcException(message: String, cause: Throwable)
  extends Exception(message, cause) {

  def this(message: String) = this(message, null)
}

