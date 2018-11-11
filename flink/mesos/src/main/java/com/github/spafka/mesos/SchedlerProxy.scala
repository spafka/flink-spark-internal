package com.github.spafka.mesos

import java.util

import akka.actor.ActorRef
import org.apache.flink.mesos.scheduler.messages._
import org.apache.mesos.{Protos, Scheduler, SchedulerDriver}


class SchedlerProxy(var  mesosActor: ActorRef)  extends  Scheduler{

  override def registered(schedulerDriver: SchedulerDriver, frameworkID: Protos.FrameworkID, masterInfo: Protos.MasterInfo): Unit = {
    mesosActor ! new Registered(frameworkID,masterInfo)
  }

  override def reregistered(schedulerDriver: SchedulerDriver, masterInfo: Protos.MasterInfo): Unit = {
    mesosActor ! new ReRegistered(masterInfo)
  }

  override def resourceOffers(schedulerDriver: SchedulerDriver, list: util.List[Protos.Offer]): Unit = {

    mesosActor ! new ResourceOffers(list)
  }

  override def offerRescinded(schedulerDriver: SchedulerDriver, offerID: Protos.OfferID): Unit = {
    mesosActor ! new OfferRescinded(offerID)
  }

  override def statusUpdate(schedulerDriver: SchedulerDriver, taskStatus: Protos.TaskStatus): Unit = {
    mesosActor ! new StatusUpdate(taskStatus)
  }

  override def frameworkMessage(schedulerDriver: SchedulerDriver, executorID: Protos.ExecutorID, slaveID: Protos.SlaveID, bytes: Array[Byte]): Unit = {
    mesosActor ! new FrameworkMessage(executorID,slaveID,bytes)
  }

  override def disconnected(schedulerDriver: SchedulerDriver): Unit = {
    mesosActor ! new Disconnected
  }

  override def slaveLost(schedulerDriver: SchedulerDriver, slaveID: Protos.SlaveID): Unit = {
    mesosActor !  new SlaveLost(slaveID)
  }

  override def executorLost(schedulerDriver: SchedulerDriver, executorID: Protos.ExecutorID, slaveID: Protos.SlaveID, status: Int): Unit = {
    mesosActor ! new ExecutorLost(executorID,slaveID,status)
  }

  override def error(schedulerDriver: SchedulerDriver, s: String): Unit = {
    mesosActor ! new Error(s)
  }
}
