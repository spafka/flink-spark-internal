package com.github.spafka.util

import java.io.IOException
import java.net.BindException

import akka.actor.{
  ActorRef,
  ActorSystem,
  Address,
  ExtendedActorSystem,
  Extension,
  ExtensionKey
}
import com.github.spafka.Constans
import com.typesafe.config.ConfigFactory
import org.apache.flink.annotation.VisibleForTesting
import org.slf4j.{Logger, LoggerFactory}

class RemoteAddressExtensionImplementation(system: ExtendedActorSystem)
  extends Extension {
  def address: Address = system.provider.getDefaultAddress
}

object RemoteAddressExtension
  extends ExtensionKey[RemoteAddressExtensionImplementation] {}

object AkkaUtils {

  def getAddress(system: ActorSystem): Address = {
    RemoteAddressExtension(system).address
  }

  def getAkkaURL(system: ActorSystem, actor: ActorRef): String = {
    val address = getAddress(system)
    actor.path.toStringWithAddress(address)
  }

  @throws[Exception]
  def startActorSystem(confPath: String, logger: Logger): ActorSystem = {
    try {
      val config = ConfigFactory.load(confPath)

      import akka.actor.ActorSystem
      val actorSystem = ActorSystem.create(Constans.AKKA_NAME, config)
      logger.info(
        "Actor system started at {}",
        AkkaUtils.getAddress(actorSystem)
      )
      actorSystem
    } catch {
      case t: Throwable ⇒
        if (t.isInstanceOf[IOException]) {
          val cause = t.getCause
          if (cause != null && t.getCause.isInstanceOf[BindException])
            throw new IOException(
              "Unable to create ActorSystem at address " + " : " + cause.getMessage,
              t
            )
        }
        throw new Exception("Could not create actor system", t)
    }
  }

  @throws[Exception]
  def startMasterActorSystem(
                              confPath: String = "master.conf",
                              logger: Logger = LoggerFactory.getLogger("akka")
                            ): ActorSystem = {
    startActorSystem(confPath, logger)
  }

  @throws[Exception]
  def startSlaveActorSystem(
                             confPath: String = "slave.conf",
                             logger: Logger = LoggerFactory.getLogger("akka")
                           ): ActorSystem = {
    startActorSystem(confPath, logger)
  }

  @VisibleForTesting def startDebugActorSystem(): ActorSystem = {
    try {
      import akka.actor.ActorSystem
      val actorSystem = ActorSystem.create("debug")
      actorSystem
    } catch {
      case t: Throwable ⇒
        throw new Exception("Could not create actor system", t)
    }
  }
}
