package io.github.spafka.flip6

import java.io.File

import org.apache.flink.runtime.entrypoint.StandaloneSessionClusterEntrypoint

object StandaloneSession {

  def main(args: Array[String]): Unit = {

    val args1 = String
      .format(
        "--configDir %s --executionMode cluster --host localhost --webui-port 8081",
       new File( Thread.currentThread.getContextClassLoader
          .getResource("flink-conf.yaml")
          .getFile).getParent
      )
      .split(" ");
    StandaloneSessionClusterEntrypoint.main(args1)
  }
}
