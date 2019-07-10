package com.github.spafka.io.netty

import java.io.IOException
import java.net.{InetAddress, InetSocketAddress}
import java.util.concurrent.ThreadFactory
import java.util.function.Function

import javax.annotation.Nullable
import org.apache.flink.configuration.{ConfigOption, ConfigOptions, Configuration, TaskManagerOptions}
import org.apache.flink.runtime.io.network.netty.{NettyBufferPool, NettyProtocol, SSLHandlerFactory}
import org.apache.flink.runtime.net.SSLUtils
import org.apache.flink.runtime.util.FatalExitExceptionHandler
import org.apache.flink.shaded.guava18.com.google.common.util.concurrent.ThreadFactoryBuilder
import org.apache.flink.shaded.netty4.io.netty.bootstrap.ServerBootstrap
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBufAllocator
import org.apache.flink.shaded.netty4.io.netty.channel.epoll.{EpollEventLoopGroup, EpollServerSocketChannel}
import org.apache.flink.shaded.netty4.io.netty.channel.nio.NioEventLoopGroup
import org.apache.flink.shaded.netty4.io.netty.channel.socket.SocketChannel
import org.apache.flink.shaded.netty4.io.netty.channel.socket.nio.NioServerSocketChannel
import org.apache.flink.shaded.netty4.io.netty.channel.{ChannelFuture, ChannelInitializer, ChannelOption}
import org.apache.flink.util.Preconditions.{checkArgument, checkState}
import org.slf4j.LoggerFactory


object NettyConfig {
  private val LOG = LoggerFactory.getLogger(classOf[NettyConfig])
  // - Config keys ----------------------------------------------------------
  val NUM_ARENAS: ConfigOption[Integer] = ConfigOptions.key("taskmanager.network.netty.num-arenas").defaultValue(new Integer(-1)).withDeprecatedKeys("taskmanager.net.num-arenas").withDescription("The number of Netty arenas.")
  val NUM_THREADS_SERVER: ConfigOption[Integer] = ConfigOptions.key("taskmanager.network.netty.server.numThreads").defaultValue(new Integer(-1)).withDeprecatedKeys("taskmanager.net.server.numThreads").withDescription("The number of Netty server threads.")
  val NUM_THREADS_CLIENT: ConfigOption[Integer] = ConfigOptions.key("taskmanager.network.netty.client.numThreads").defaultValue(new Integer(-1)).withDeprecatedKeys("taskmanager.net.client.numThreads").withDescription("The number of Netty client threads.")
  val CONNECT_BACKLOG: ConfigOption[Integer] = ConfigOptions.key("taskmanager.network.netty.server.backlog").defaultValue(new Integer(0))
  val CLIENT_CONNECT_TIMEOUT_SECONDS: ConfigOption[Integer] = ConfigOptions.key("taskmanager.network.netty.client.connectTimeoutSec").defaultValue(new Integer(120))

  val SEND_RECEIVE_BUFFER_SIZE: ConfigOption[Integer] = ConfigOptions.key("taskmanager.network.netty.sendReceiveBufferSize").defaultValue(new Integer(0)).withDeprecatedKeys("taskmanager.net.sendReceiveBufferSize").withDescription("The Netty send and receive buffer size. This defaults to the system buffer size" + " (cat /proc/sys/net/ipv4/tcp_[rw]mem) and is 4 MiB in modern Linux.")
  val TRANSPORT_TYPE: ConfigOption[String] = ConfigOptions.key("taskmanager.network.netty.transport").defaultValue("nio").withDeprecatedKeys("taskmanager.net.transport").withDescription("The Netty transport type, either \"nio\" or \"epoll\"")

  // ------------------------------------------------------------------------
  private[netty] object TransportType extends Enumeration {
    type TransportType = Value
    val NIO, EPOLL, AUTO = Value
  }

  private[netty] val SERVER_THREAD_GROUP_NAME = "Flink Netty Server"
  private[netty] val CLIENT_THREAD_GROUP_NAME = "Flink Netty Client"
}

class NettyConfig(val serverAddress: InetAddress, val serverPort: Int, val memorySegmentSize: Int, val numberOfSlots: Int, val config: Configuration) {

  checkArgument(serverPort >= 0 && serverPort <= 65536, "Invalid port number.", null)
  checkArgument(memorySegmentSize > 0, "Invalid memory segment size.", null)
  checkArgument(numberOfSlots > 0, "Number of slots", null)

  NettyConfig.LOG.info(this.toString)


  private[netty] def getServerAddress = serverAddress

  private[netty] def getServerPort = serverPort

  private[netty] def getMemorySegmentSize = memorySegmentSize

  def getNumberOfSlots: Int = numberOfSlots

  // Getters
  def getServerConnectBacklog = config.getInteger(NettyConfig.CONNECT_BACKLOG)

  def getNumberOfArenas: Int = { // default: number of slots
    val configValue = config.getInteger(NettyConfig.NUM_ARENAS)
    if (configValue == -1) numberOfSlots
    else configValue
  }

  def getServerNumThreads: Int = { // default: number of task slots
    val configValue = config.getInteger(NettyConfig.NUM_THREADS_SERVER)
    if (configValue == -1) numberOfSlots
    else configValue
  }

  def getClientNumThreads: Int = {
    val configValue = config.getInteger(NettyConfig.NUM_THREADS_CLIENT)
    if (configValue == -1) numberOfSlots
    else configValue
  }

  def getClientConnectTimeoutSeconds: Int = config.getInteger(NettyConfig.CLIENT_CONNECT_TIMEOUT_SECONDS)

  def getSendAndReceiveBufferSize: Int = config.getInteger(NettyConfig.SEND_RECEIVE_BUFFER_SIZE)

  def getTransportType: NettyConfig.TransportType.TransportType = {
    val transport = config.getString(NettyConfig.TRANSPORT_TYPE)
    transport match {
      case "nio" =>
        NettyConfig.TransportType.NIO
      case "epoll" =>
        NettyConfig.TransportType.EPOLL
      case _ =>
        NettyConfig.TransportType.AUTO
    }
  }

  @Nullable
  @throws[Exception]
  def createClientSSLEngineFactory: SSLHandlerFactory = if (getSSLEnabled) SSLUtils.createInternalClientSSLEngineFactory(config)
  else null

  @Nullable
  @throws[Exception]
  def createServerSSLEngineFactory: SSLHandlerFactory = if (getSSLEnabled) SSLUtils.createInternalServerSSLEngineFactory(config)
  else null

  def getSSLEnabled: Boolean = config.getBoolean(TaskManagerOptions.DATA_SSL_ENABLED) && SSLUtils.isInternalSSLEnabled(config)

  def isCreditBasedEnabled: Boolean = config.getBoolean(TaskManagerOptions.NETWORK_CREDIT_MODEL)

  def getConfig: Configuration = config


}


object NettyServer {
  private val THREAD_FACTORY_BUILDER = new ThreadFactoryBuilder().setDaemon(true).setUncaughtExceptionHandler(FatalExitExceptionHandler.INSTANCE)
  private val LOG = LoggerFactory.getLogger(classOf[NettyServer])

  def getNamedThreadFactory(name: String): ThreadFactory = THREAD_FACTORY_BUILDER.setNameFormat(name + " Thread %d").build

  private[netty] class ServerChannelInitializer(val protocol: NettyProtocol, val sslHandlerFactory: SSLHandlerFactory) extends ChannelInitializer[SocketChannel] {
    @throws[Exception]
    override def initChannel(channel: SocketChannel): Unit = {
      channel.pipeline.addLast(protocol.getServerChannelHandlers: _*)
    }
  }

}

class NettyServer private[netty](val config: NettyConfig) {


  private var bootstrap: ServerBootstrap = null
  private var bindFuture: ChannelFuture = null
  private var localAddress: InetSocketAddress = null

  @throws[IOException]
  private[netty] def init(protocol: NettyProtocol, nettyBufferPool: NettyBufferPool):Int = init(nettyBufferPool,
    (sslHandlerFactory: SSLHandlerFactory) => new NettyServer.ServerChannelInitializer(protocol, sslHandlerFactory))

  @throws[IOException]
  private[netty] def init(nettyBufferPool: NettyBufferPool, channelInitializer: Function[SSLHandlerFactory, NettyServer.ServerChannelInitializer]) = {
    checkState(bootstrap == null, "Netty server has already been initialized.",null)
    val start = System.nanoTime
    bootstrap = new ServerBootstrap()

    initNioBootstrap()
    NettyServer.LOG.info("Transport type 'auto': using NIO.")

    // Configuration
    // Server bind address
    bootstrap.localAddress(config.getServerAddress, config.getServerPort)
    // Pooled allocators for Netty's ByteBuf instances
    bootstrap.option(ChannelOption.ALLOCATOR, nettyBufferPool)
    bootstrap.childOption[ByteBufAllocator](ChannelOption.ALLOCATOR, nettyBufferPool)
    if (config.getServerConnectBacklog > 0) bootstrap.option[Integer](ChannelOption.SO_BACKLOG,1)
    // Receive and send buffer size
    val receiveAndSendBufferSize = config.getSendAndReceiveBufferSize
    if (receiveAndSendBufferSize > 0) {
      bootstrap.childOption[Integer](ChannelOption.SO_SNDBUF, receiveAndSendBufferSize)
      bootstrap.childOption[Integer](ChannelOption.SO_RCVBUF, receiveAndSendBufferSize)
    }
    // Low and high water marks for flow control
    // hack around the impossibility (in the current netty version) to set both watermarks at
    // the same time:
    val defaultHighWaterMark = 64 * 1024 // from DefaultChannelConfig (not exposed)
    val newLowWaterMark = config.getMemorySegmentSize + 1
    val newHighWaterMark = 2 * config.getMemorySegmentSize
    if (newLowWaterMark > defaultHighWaterMark) {
      bootstrap.childOption[Integer](ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, newHighWaterMark)
      bootstrap.childOption[Integer](ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, newLowWaterMark)
    }
    else { // including (newHighWaterMark < defaultLowWaterMark)
      bootstrap.childOption[Integer](ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, newLowWaterMark)
      bootstrap.childOption[Integer](ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, newHighWaterMark)
    }

    // Child channel pipeline for accepted connections

    // Start Server
    bindFuture = bootstrap.bind().syncUninterruptibly
    localAddress = bindFuture.channel.localAddress.asInstanceOf[InetSocketAddress]
    val duration = (System.nanoTime - start) / 1000000
    NettyServer.LOG.info("Successful initialization (took {} ms). Listening on SocketAddress {}.", duration, localAddress)
    localAddress.getPort
  }

  private[netty] def getConfig = config

  private[netty] def getBootstrap = bootstrap

  private[netty] def shutdown(): Unit = {
    val start = System.nanoTime
    if (bindFuture != null) {
      bindFuture.channel.close.awaitUninterruptibly
      bindFuture = null
    }
    if (bootstrap != null) {
      if (bootstrap.group() != null) bootstrap.group().shutdownGracefully
      bootstrap = null
    }
    val duration = (System.nanoTime - start) / 1000000
    NettyServer.LOG.info("Successful shutdown (took {} ms).", duration)
  }

  private def initNioBootstrap(): Unit = { // Add the server port number to the name in order to distinguish
    // multiple servers running on the same host.
    val name = NettyConfig.SERVER_THREAD_GROUP_NAME + " (" + config.getServerPort + ")"
    val nioGroup = new NioEventLoopGroup(config.getServerNumThreads, NettyServer.getNamedThreadFactory(name))
    bootstrap.group(nioGroup).channel(classOf[NioServerSocketChannel])
  }

  private def initEpollBootstrap(): Unit = {
    val name = NettyConfig.SERVER_THREAD_GROUP_NAME + " (" + config.getServerPort + ")"
    val epollGroup = new EpollEventLoopGroup(config.getServerNumThreads, NettyServer.getNamedThreadFactory(name))
    bootstrap.group(epollGroup).channel(classOf[EpollServerSocketChannel])
  }
}