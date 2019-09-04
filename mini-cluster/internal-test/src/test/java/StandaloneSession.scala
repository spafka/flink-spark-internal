import org.apache.flink.runtime.entrypoint.StandaloneSessionClusterEntrypoint

object StandaloneSession {

  def main(args: Array[String]): Unit = {

    val args1 = String
      .format(
        "--configDir %s --executionMode cluster --host localhost --webui-port 80",
        Thread.currentThread.getContextClassLoader
          .getResource("flink-conf.yaml")
          .getFile + "/.."
      )
      .split(" ");
    StandaloneSessionClusterEntrypoint.main(args1)
  }
}
