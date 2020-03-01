package io.github.spafka.flip6

import java.io.File

object TaskManagerRunner {

  def main(args: Array[String]): Unit = {
    org.apache.flink.runtime.taskexecutor.TaskManagerRunner
      .main(s"--configDir ${new File(Thread.currentThread.getContextClassLoader
        .getResource("flink-conf.yaml")
        .getFile).getParent}".split(" "))
  }
}
