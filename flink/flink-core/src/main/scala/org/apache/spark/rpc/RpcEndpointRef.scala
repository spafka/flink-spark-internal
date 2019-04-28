package org.apache.spark.rpc

import org.apache.spark.{RpcConf, RpcException}
import org.apache.spark.util.RpcUtils
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * A reference for a remote [[RpcEndpoint]]. [[RpcEndpointRef]] is thread-safe.
  * RpcEndpointRef是一个对RpcEndpoint的远程引用对象，通过它可以向远程的RpcEndpoint端发送消息以进行通信。
  */
abstract class RpcEndpointRef(conf: RpcConf) extends Serializable {

  private val log = LoggerFactory.getLogger(classOf[RpcEndpointRef])

  // 重试次数相关
  private[this] val maxRetries = RpcUtils.numRetries(conf)
  private[this] val retryWaitMs = RpcUtils.retryWaitMs(conf)
  private[this] val defaultAskTimeout = RpcUtils.askRpcTimeout(conf)

  /**
    * return the address for the [[RpcEndpointRef]]
    */
  def address: RpcAddress

  def name: String

  /**
    * Sends a one-way asynchronous message. Fire-and-forget semantics.
    * Endpoint 不需要回应
    */
  def send(message: Any): Unit

  def ask[T: ClassTag](message: Any, timeout: RpcTimeout): Future[T]

  def ask[T: ClassTag](message: Any): Future[T] = ask(message, defaultAskTimeout)

  def askWithRetry[T: ClassTag](message: Any): T = askWithRetry(message, defaultAskTimeout)

  def askWithRetry[T: ClassTag](message: Any, timeout: RpcTimeout): T = {
    // TODO: Consider removing multiple attempts
    var attempts = 0
    var lastException: Exception = null
    while (attempts < maxRetries) {
      attempts += 1
      try {
        val future = ask[T](message, timeout)
        val result = timeout.awaitResult(future)
        if (result == null) {
          throw new RpcException("RpcEndpoint returned null")
        }
        return result
      } catch {
        case ie: InterruptedException => throw ie
        case e: Exception =>
          lastException = e
          log.warn(s"Error sending message [message = $message] in $attempts attempts", e)
      }

      if (attempts < maxRetries) {
        Thread.sleep(retryWaitMs)
      }
    }

    throw new RpcException(
      s"Error sending message [message = $message]", lastException)
  }

}
