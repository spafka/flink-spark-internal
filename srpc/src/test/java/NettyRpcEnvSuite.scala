package org.apache.spark.rpc.netty

import org.apache.spark.SparkConf
import org.apache.spark.rpc.{RpcEndpoint, RpcEnv, RpcEnvConfig}
import org.junit.{Before, Test}
class NettyRpcEnvSuite {
  def createRpcEnv(conf: SparkConf,
                   name: String,
                   port: Int,
                   clientMode: Boolean = false): RpcEnv = {
    new NettyRpcEnvFactory()
      .create(RpcEnvConfig(conf, name, "localhost", port, null, clientMode))
  }

  var env: RpcEnv = _

  @Before
  def init = {
    val conf = new SparkConf()
    env = createRpcEnv(conf, "localhost", 1024, false)
  }

  @Test
  def akkaTest = {
    import org.apache.spark.rpc.RpcAddress

    val ref = env.setupEndpoint("test_endpoint", new RpcEndpoint {
      override def receive = {
        case x: String => println(s"String $x")
        case y: Int ⇒ println(s"Int $y")
      }

      override val rpcEnv: RpcEnv = env
    })
    val conf = new SparkConf()
    val newRpcEnv = new NettyRpcEnvFactory()
      .create(RpcEnvConfig(conf, "test", "localhost", 1025, null, true))

    val ref2 = newRpcEnv.setupEndpointRef(
      "local",
      RpcAddress("localhost", 1024),
      "test_endpoint"
    )

    ref2.send("111")

    Thread.sleep(1000000)
  }

}
