package com.github.spafka.util

import java.io.IOException
import java.net.BindException

import akka.actor.{ActorSystem, Address, ExtendedActorSystem, Extension, ExtensionKey}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.Logger

class RemoteAddressExtensionImplementation(system: ExtendedActorSystem) extends Extension {
  def address: Address = system.provider.getDefaultAddress
}

object RemoteAddressExtension extends ExtensionKey[RemoteAddressExtensionImplementation] {}


object AkkaUtils {



  def createActorSystem(akkaConfig: Config): ActorSystem = {
    // Initialize slf4j as logger of Akka's Netty instead of java.util.logging (FLINK-1650)
    ActorSystem.create("flink", akkaConfig)
  }

  def getAddress(system: ActorSystem): Address = {
    RemoteAddressExtension(system).address
  }


  @throws[Exception]
  def startActorSystem(configuration: Map[String, String] ,confPath: String, logger: Logger): ActorSystem = {
    try {
      val config = ConfigFactory.load(confPath)

      import akka.actor.ActorSystem
      val actorSystem =   ActorSystem.create("flink",config)
      logger.info("Actor system started at {}", AkkaUtils.getAddress(actorSystem))
      actorSystem
    } catch {
      case t: Throwable =>
        if (t.isInstanceOf[IOException]) {
          val cause = t.getCause
          if (cause != null && t.getCause.isInstanceOf[BindException]) throw new IOException("Unable to create ActorSystem at address "  + " : " + cause.getMessage, t)
        }
        throw new Exception("Could not create actor system", t)
    }
  }
}
