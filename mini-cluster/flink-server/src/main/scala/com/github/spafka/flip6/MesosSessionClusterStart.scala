package com.github.spafka.flip6

import org.apache.flink.mesos.entrypoint.MesosSessionClusterEntrypoint

object MesosSessionClusterStart {


  def main(args: Array[String]): Unit = {

    val argss = "-Dmesos.master=localhost:5050 -Djobmanager.heap.mb=1024 -Djobmanager.rpc.port=6123 -Drest.port=8081 -Dmesos.resourcemanager.tasks.mem=4096 -Dtaskmanager.heap.mb=3500 -Dtaskmanager.numberOfTaskSlots=2 -Dparallelism.default=10".split(" ")
    MesosSessionClusterEntrypoint.main(argss)


  }

}
