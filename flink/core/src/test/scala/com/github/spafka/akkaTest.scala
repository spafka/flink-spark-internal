package com.github.spafka

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props}
import com.github.spafka.util.AkkaUtils
import org.junit.Test
import org.slf4j.LoggerFactory

class akkaTest {

  class HelloActor extends Actor {
    override def receive = {
      case msg: String ⇒ println("echo " + msg)
      case msg ⇒ println("none string msg")
      case _ ⇒
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
    import akka.actor.ActorSelection

    val log = LoggerFactory.getLogger(classOf[akkaTest])
    val masterActor = AkkaUtils.startMasterActorSystem(logger = log)
    println(masterActor)

    val helloEndPoint = masterActor.actorOf(Props(new HelloActor), "hello")

    println(helloEndPoint)

    val slaveActor = AkkaUtils.startSlaveActorSystem(logger = log)

    val helloSel: ActorSelection =
      slaveActor.actorSelection("akka.tcp://flink@127.0.0.1:6332/user/hello")

    import akka.actor.{ActorIdentity, Identify}
    import akka.pattern.ask
    import akka.util.Timeout

    import scala.concurrent.duration._
    implicit val timeout: Timeout = (5 seconds)

    val identify = new Identify(42)
    val a = helloSel ? identify
    val eventualIdentity = a.mapTo[ActorIdentity]

    implicit val sc = slaveActor.dispatcher
    eventualIdentity.onComplete {
      case scala.util.Success(value) ⇒ println(value.getRef)
      case scala.util.Failure(exception) ⇒ println(exception.getMessage)
    }
    println(helloSel)

    TimeUnit.SECONDS.sleep(1)
  }

}
