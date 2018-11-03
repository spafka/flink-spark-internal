package com.github.spafka

import java.net.InetAddress

import akka.actor.{Actor, ActorRef, Props}
import com.github.spafka.util.AkkaUtils
import grizzled.slf4j.Logger

class Master(protected val map: Map[String, String]) extends Actor {


  // fixme add more execute
  val logger = Logger(classOf[Master])

  override def receive: Receive = {
    case e: RigistMessage => {
      print(e)
    }
    case t: Task => {
      t.run
    }
    case x:AskMessage => sender() ! ReplyMessage
  }

  override def preStart(): Unit = {
    super.preStart();
    logger.info(s"Start Master on ${InetAddress.getLocalHost().getHostName} on Port ${map.getOrElse("port", "6123")}")
  }
}


object Master {
  def main(args: Array[String]): Unit = {

    val LOG = Logger(classOf[Master])

    val conf = Map("port" -> "6123")
    val actorSystem = AkkaUtils
      .startActorSystem(conf,
        "master.conf",
        LOG.logger)

    val masterActor: ActorRef = actorSystem.actorOf(Props(classOf[Master], conf), "master")

    println(masterActor.path)

  }
}