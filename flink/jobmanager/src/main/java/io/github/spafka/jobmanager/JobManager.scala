package io.github.spafka.jobmanager

import java.net.InetAddress

import akka.actor.{Actor, ActorRef, Props}
import com.github.spafka.rpc.message.{AskMessage, RigistMessage, Task}
import com.github.spafka.rpc.message.{AskMessage, ReplyMessage, Task}
import grizzled.slf4j.Logger
import io.github.spafka.rpc.message.{AskMessage, ReplyMessage, RigistMessage, Task}
import io.github.spafka.util.AkkaUtils

class JobManager(protected val map: Map[String, String]) extends Actor {


  // fixme add more execute
  val logger = Logger(classOf[JobManager])

  override def receive: Receive = {
    case e: RigistMessage => {
      print(e)
    }
    case t: Task => {
      t.run
    }
    case x: AskMessage => sender() ! ReplyMessage
  }

  override def preStart(): Unit = {
    super.preStart();
    logger.info(s"Start Master on ${InetAddress.getLocalHost().getHostName} on Port ${map.getOrElse("port", "6123")}")
  }
}


object JobManager {
  def main(args: Array[String]): Unit = {

    val LOG = Logger(classOf[JobManager])

    val conf = Map("port" -> "6123")
    val actorSystem = AkkaUtils
      .startActorSystem("master.conf", LOG.logger)

    val masterActor: ActorRef = actorSystem.actorOf(Props(classOf[JobManager], conf), "master")

    println(masterActor.path)

  }
}