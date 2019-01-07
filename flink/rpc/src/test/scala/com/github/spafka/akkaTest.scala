package com.github.spafka

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props}
import com.github.spafka.util.AkkaUtils
import grizzled.slf4j.Logger
import org.junit.Test
import org.slf4j.LoggerFactory

class akkaTest {

  class HelloActor extends Actor {
    override def receive = {
      case msg: String ⇒ println("echo " + msg)
      case msg ⇒ println("none string msg")
    }
  }

  @Test def createActorSysTerm(): Unit = {
    val actorSystem = AkkaUtils.startDebugActorSystem()
    println(actorSystem)


    val hello = actorSystem.actorOf(Props(new HelloActor()))

    hello ! "string"
    hello ! 1

    println(hello)
  }

  @Test def testRemoteActorSysTerm(): Unit = {

    val log = LoggerFactory.getLogger(classOf[akkaTest])
    val masterActor = AkkaUtils.startMasterActorSystem(logger = log)
    println(masterActor)

    val helloEndPoint = masterActor.actorOf(Props(new HelloActor), "hello")

    println(helloEndPoint)

    val slave = AkkaUtils.startSlaveActorSystem(logger = log)

    val helloRef = slave.actorSelection("akka.tcp://master@127.0.0.1:6332/user/hello")

    helloRef ! "string"
    helloRef ! 1

    println(helloRef)

    TimeUnit.SECONDS.sleep(1)
  }


}
