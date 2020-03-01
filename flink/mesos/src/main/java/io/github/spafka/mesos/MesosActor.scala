package io.github.spafka.mesos

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, Props}
import com.netflix.fenzo.functions.Action1
import com.netflix.fenzo.{TaskScheduler, VirtualMachineLease}
import grizzled.slf4j.Logger
import io.github.spafka
import io.github.spafka.util.AkkaUtils
import org.apache.flink.mesos.scheduler.TaskSchedulerBuilder
import org.apache.flink.mesos.scheduler.messages.{Disconnected, ReRegistered, Registered}
import org.apache.mesos.{MesosSchedulerDriver, Protos}

import scala.concurrent.duration.FiniteDuration


class MesosActor extends Actor {

  private var connectionMonitor: ActorRef = _

  private var taskRouter: ActorRef = _

  private var launchCoordinator: ActorRef = _

  private var reconciliationCoordinator: ActorRef = _

  private def registered(message: Registered): Unit = {
    connectionMonitor ! message
    taskRouter ! message
    launchCoordinator ! message
    reconciliationCoordinator ! message
  }

  private def reregistered(message: ReRegistered): Unit = {
    connectionMonitor ! message
    taskRouter ! message
    launchCoordinator ! message
    reconciliationCoordinator ! message
  }

  private def disconnected(message: Disconnected): Unit = {
    connectionMonitor ! message
    taskRouter ! message
    launchCoordinator ! message
    reconciliationCoordinator ! message
  }

  private def error(message: Error): Unit = {
    connectionMonitor ! message
    taskRouter ! message
    launchCoordinator ! message
    reconciliationCoordinator ! message
  }

  override def receive: Receive = {
    case x: Registered => registered(x)
    case x: ReRegistered => reregistered(x)
    case x: Disconnected => disconnected(x)
  }

  override def preStart(): Unit = {

    // com.github.spafka.mesos schedler
    val scheduler = new SchedlerProxy(self)

    val frameworkInfo = Protos.FrameworkInfo.newBuilder.setHostname("localhost")
    val masterUrl = "zk://127.0.0.1:2181/com.github.spafka.mesos"
    val failoverTimeout = FiniteDuration.apply(1, TimeUnit.SECONDS)
    frameworkInfo.setFailoverTimeout(failoverTimeout.toSeconds)
    frameworkInfo.setName("spafka")
    frameworkInfo.setUser("*")

    // com.github.spafka.mesos schedler
    val schedulerDriver = new MesosSchedulerDriver(scheduler, frameworkInfo.build, masterUrl, false)
    schedulerDriver.start()

    // MESOS 连接信息FSM
    connectionMonitor = context.actorOf(Props(classOf[ConnectionMonitor]))
    // MESOS Executor FSM
    launchCoordinator = context.actorOf(Props(classOf[LaunchCoordinator], self, schedulerDriver, createOptimizer()))
    //
    taskRouter = context.actorOf(spafka.mesos.Tasks.createActorProps(classOf[Tasks], self, schedulerDriver, classOf[TaskMonitor]))
    //
    reconciliationCoordinator = context.actorOf(Props(classOf[ReconciliationCoordinator], schedulerDriver))

    connectionMonitor ! new ConnectionMonitor.Start
  }

  /**
    * Creates the Fenzo optimizer (builder).
    * The builder is an indirection to facilitate unit testing of the Launch Coordinator.
    */
  private def createOptimizer(): TaskSchedulerBuilder = {
    return new TaskSchedulerBuilder() {
      private val builder = new TaskScheduler.Builder

      override

      def withLeaseRejectAction(action: Action1[VirtualMachineLease]): TaskSchedulerBuilder = {
        builder.withLeaseRejectAction(action)
        this
      }

      override def build: TaskScheduler = builder.build

      override def withRejectAllExpiredOffers(): TaskSchedulerBuilder = ???

      override def withLeaseOfferExpirySecs(l: Long): TaskSchedulerBuilder = ???
    }
  }
}

object MesosActor {


  def main(args: Array[String]): Unit = {

    val LOG = Logger(getClass)
    val actorSystem = AkkaUtils.startActorSystem("master.conf", LOG.logger)
    val mesosActor: ActorRef = actorSystem.actorOf(Props(classOf[MesosActor]), "master")

    TimeUnit.SECONDS.sleep(100000)

  }
}
