package com.github.spafka.rpc

trait RpcServer {

  import java.util.concurrent.CompletableFuture

  /**
    * Return a future which is completed when the rpc endpoint has been terminated.
    *
    * @return Future indicating when the rpc endpoint has been terminated
    */
  def getTerminationFuture: CompletableFuture[Void]
}
