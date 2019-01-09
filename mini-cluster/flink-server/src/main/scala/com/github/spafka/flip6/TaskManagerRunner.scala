package com.github.spafka.flip6

object TaskManagerRunner {

  def main(args: Array[String]): Unit = {
    org.apache.flink.runtime.taskexecutor.TaskManagerRunner.main(s"--configDir ${"e2e\\flink\\src\\main\\resources"}".split(" "))
  }
}
