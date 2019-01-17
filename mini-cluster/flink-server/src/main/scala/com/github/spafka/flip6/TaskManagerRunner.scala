package com.github.spafka.flip6

object TaskManagerRunner {

  def main(args: Array[String]): Unit = {
    org.apache.flink.runtime.taskexecutor.TaskManagerRunner
      .main(s"--configDir ${
        Thread.currentThread.getContextClassLoader
          .getResource("flink-conf.yaml")
          .getFile + "/.."
      }".split(" "))
  }
}
