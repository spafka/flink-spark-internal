package org.apache.spark.rpc

import org.apache.spark.RpcException

private[rpc] class RpcEndpointNotFoundException(uri: String)
  extends RpcException(s"Cannot find endpoint: $uri")
