package com.github.spafka.taskmanager

import akka.actor.SupervisorStrategy.Escalate
import akka.actor.{Actor, OneForOneStrategy}
import akka.pattern.ask
import akka.util.Timeout
import com.github.spafka._
import com.github.spafka.jobmanager.JobManager
import com.github.spafka.message._
import com.github.spafka.util.AkkaUtils
import grizzled.slf4j.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, _}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class TaskManager extends Actor {

  // fixme add more executor
  override def receive: Receive = {

    case e: RigistMessage => {
      print(e)
    }
    case e: BreakMessage => {
      context.system.scheduler.scheduleOnce(1 seconds, self, RigistMessage)(context.dispatcher)
    }

  }

  override def supervisorStrategy = {
    OneForOneStrategy() {
//      case BrokenPlateException => Resume
//      case DrunkenFoolException => Restart
//      case RestaurantFireError =>Escalate
//      case TiredChefException => Stop
      case _ => Escalate
    }
  }

}


object TaskManager {
  def main(args: Array[String]): Unit = {


    val LOG = Logger(classOf[JobManager])

    val conf = Map("port" -> "6124")
    val actorSystem = AkkaUtils
      .startActorSystem(conf, "slave.conf",
        LOG.logger)

    // actorSelection 连接远程的actor
    val ref = actorSystem.actorSelection(s"akka.tcp://flink@127.0.0.1:6332/user/master")

    ref ! RigistMessage("localhost", null)
    ref ! Task(tdd = new TaskDesc {

      override def run: Unit = {
        while (true) {
          println("task invokeing!!!! ")
        }
      }
    })

    implicit val timeout = new Timeout(Duration.create(1, "seconds"))
    ref ? AskMessage() onComplete {
      case failed: Failure[akka.pattern.AskTimeoutException] => println(s"failed=" + failed)
      case Success(result) => println(s"$result")
      case Failure(exception) =>
    }


  }
}