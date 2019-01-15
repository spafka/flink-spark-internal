package com.github.spafka.util

object RpcUtils {

  import java.util

  import com.github.spafka.rpc.RpcGateway

  def extractImplementedRpcGateways(
    clazz: Class[_]
  ): util.Set[Class[_ <: RpcGateway]] = {
    import java.util
    val interfaces: util.HashSet[Class[_ <: RpcGateway]] =
      new util.HashSet[Class[_ <: RpcGateway]]

    var clazzz = clazz
    while ({
      clazzz != null
    }) {
      for (interfaze <- clazz.getInterfaces) {
        if (classOf[RpcGateway].isAssignableFrom(interfaze))
          interfaces.add(interfaze.asInstanceOf[Class[_ <: RpcGateway]])
      }
      clazzz = clazzz.getSuperclass
    }
    interfaces
  }

}
