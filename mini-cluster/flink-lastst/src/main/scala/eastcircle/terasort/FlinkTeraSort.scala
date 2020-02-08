package eastcircle.terasort

import io.github.spafka.spark.util.Utils
import org.apache.flink.api.common.functions.Partitioner
import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala._
import org.apache.flink.api.scala.hadoop.mapreduce.HadoopOutputFormat
import org.apache.flink.configuration._
import org.apache.flink.hadoopcompatibility.scala.HadoopInputs
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job


class OptimizedFlinkTeraPartitioner(underlying: TotalOrderPartitioner) extends Partitioner[OptimizedText] {
  def partition(key: OptimizedText, numPartitions: Int): Int = {
    underlying.getPartition(key.getText())
  }
}


object FlinkTeraSort {

  implicit val textOrdering = new Ordering[Text] {
    override def compare(a: Text, b: Text) = a.compareTo(b)
  }

  def main(args: Array[String]) {
    if (args.size != 3) {
      println("Usage: FlinkTeraSort hdfs inputPath outputPath #partitions ")
      return
    }

    val runtime = Utils.timeTakenMs {

      //val env = ExecutionEnvironment.getExecutionEnvironment
      val configuration = new Configuration
      configuration.setInteger(RestOptions.PORT, 8080)
      configuration.setInteger(TaskManagerOptions.NUM_TASK_SLOTS, 100)

      configuration.setString(NettyShuffleEnvironmentOptions.NETWORK_BUFFERS_MEMORY_MIN, "1m");
      configuration.setInteger(NettyShuffleEnvironmentOptions.NETWORK_NUM_BUFFERS, 4096)
      configuration.setFloat(NettyShuffleEnvironmentOptions.NETWORK_BUFFERS_MEMORY_FRACTION, 0.5f)
      configuration.setString(CoreOptions.TMP_DIRS, "f://flink-tmp")

      val env = ExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
      env.getConfig.enableObjectReuse()


      val inputPath = args(0)
      val outputPath = args(1)
      val partitions = args(2).toInt

      val mapredConf = new JobConf()
      mapredConf.set("mapreduce.input.fileinputformat.inputdir", inputPath)
      mapredConf.set("mapreduce.output.fileoutputformat.outputdir", outputPath)
      mapredConf.setInt("mapreduce.job.reduces", partitions)

      val partitionFile = new Path(outputPath, TeraInputFormat.PARTITION_FILENAME)
      val jobContext = Job.getInstance(mapredConf)
      TeraInputFormat.writePartitionFile(jobContext, partitionFile)
      val partitioner = new OptimizedFlinkTeraPartitioner(new TotalOrderPartitioner(mapredConf, partitionFile))

      env.createInput(HadoopInputs.readHadoopFile(new TeraInputFormat(), classOf[Text], classOf[Text], outputPath, jobContext)).name("hadoopfile")
        .map(tp => {
          (new OptimizedText(tp._1), tp._2)
        })
        .partitionCustom(partitioner, 0).sortPartition(0, Order.ASCENDING)
        .map(tp => (tp._1.getText, tp._2))
        .output(new HadoopOutputFormat[Text, Text](new TeraOutputFormat(), jobContext))
      env.execute("TeraSort")
    }


    println(runtime)
  }
}
