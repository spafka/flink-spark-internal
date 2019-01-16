package org.github.spafka.util

import java.net.{Inet4Address, InetAddress, NetworkInterface}
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit.NANOSECONDS
import java.util.concurrent.locks.Lock

import grizzled.slf4j.Logger
import org.apache.commons.lang3.SystemUtils
import sun.misc.Unsafe

import scala.collection.JavaConverters._

object Utils {

  private val logger = Logger(Utils.getClass)

  /** Records the duration of running `body`. */
  def timeTakenMs[T](body: => T): (T, Long) = {
    val startTime = System.nanoTime()
    val result = body
    val endTime = System.nanoTime()
    (result, math.max(NANOSECONDS.toMillis(endTime - startTime), 0))
  }

  // call by name 函数,
  def time[T](body: => T): (T) = {
    val startTime = System.nanoTime()
    val result = body
    val endTime = System.nanoTime()

    println(s"cost ${(endTime - startTime) / 1000 / 1000} ms")
    result

  }

  def time[T](desc: String, body: => T): (T) = {
    val startTime = System.nanoTime()
    val result = body
    val endTime = System.nanoTime()

    println(s"$desc cost ${(endTime - startTime) / 1000 / 1000} ms")
    result

  }

  /** lock and unlock  */
  def lock[T](lock: Lock, body: => T) = {
    try {
      lock.lock()
      body
    } finally {
      lock.unlock()
    }
  }

  /**
    * Get the local host's IP address in dotted-quad format (e.g. 1.2.3.4).
    * Note, this is typically not used from within core spark.
    */
  def findLocalInetAddress(): InetAddress = {
    val defaultIpOverride = System.getenv("SPARK_LOCAL_IP")
    if (defaultIpOverride != null) {
      InetAddress.getByName(defaultIpOverride)
    } else {
      val address = InetAddress.getLocalHost
      if (address.isLoopbackAddress) {
        // Address resolves to something like 127.0.1.1, which happens on Debian; try to find
        // a better address using the local network interfaces
        // getNetworkInterfaces returns ifs in reverse order compared to ifconfig output order
        // on unix-like system. On windows, it returns in index order.
        // It's more proper to pick ip address following system output order.
        val activeNetworkIFs =
          NetworkInterface.getNetworkInterfaces.asScala.toSeq
        val reOrderedNetworkIFs =
          if (SystemUtils.IS_OS_WINDOWS) activeNetworkIFs
          else activeNetworkIFs.reverse

        for (ni <- reOrderedNetworkIFs) {
          val addresses = ni.getInetAddresses.asScala
            .filterNot(
              addr => addr.isLinkLocalAddress || addr.isLoopbackAddress
            )
            .toSeq
          if (addresses.nonEmpty) {
            val addr = addresses
              .find(_.isInstanceOf[Inet4Address])
              .getOrElse(addresses.head)
            // because of Inet6Address.toHostName may add interface at the end if it knows about it
            val strippedAddress = InetAddress.getByAddress(addr.getAddress)
            // We've found an address that looks reasonable!
            logger.warn(
              "Your hostname, " + InetAddress.getLocalHost.getHostName + " resolves to" +
                " a loopback address: " + address.getHostAddress + "; using " +
                strippedAddress.getHostAddress + " instead (on interface " + ni.getName + ")"
            )
            logger.warn(
              "Set SPARK_LOCAL_IP if you need to bind to another address"
            )
            return strippedAddress
          }
        }
        logger.warn(
          "Your hostname, " + InetAddress.getLocalHost.getHostName + " resolves to" +
            " a loopback address: " + address.getHostAddress + ", but we couldn't find any" +
            " external IP address!"
        )
        logger.warn("Set SPARK_LOCAL_IP if you need to bind to another address")
      }
      address
    }
  }

  def getUnSafe: Unsafe = {
    var unsafe: Unsafe = null
    try {
      val filed = classOf[Unsafe].getDeclaredField("theUnsafe")

      filed.setAccessible(true)
      unsafe = filed.get(null).asInstanceOf[Unsafe]
    } catch {
      case e =>
    }
    unsafe
  }

  def setThreadName(t: Thread, name: String): Thread = {
    t.setName(name)
    t
  }

}
