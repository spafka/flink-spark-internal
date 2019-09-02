package eastcircle.terasort

import io.github.spafka.spark.util.Utils
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.{Partitioner, SparkConf, SparkContext}

class SparkTeraRangePartitioner(underlying: TotalOrderPartitioner,
                                partitions: Int) extends Partitioner {
  def numPartitions: Int = partitions

  def getPartition(key: Any): Int = {
    val textKey = key.asInstanceOf[Text]
    underlying.getPartition(textKey)
  }
}

object SparkTeraSort {

  implicit val textOrdering = new Ordering[Text] {
    override def compare(a: Text, b: Text) = a.compareTo(b)
  }

  def main(args: Array[String]) {


    val time = Utils.timeTakenMs {
      val inputPath = args(0)
      val outputPath = args(1)
      val partitions = args(2).toInt


      val conf = new SparkConf().setAppName("TeraSort").setMaster("local[*]")
      val sc = new SparkContext(conf)


      val hadoopConf = new JobConf()
      hadoopConf.set("mapreduce.input.fileinputformat.inputdir", inputPath)
      hadoopConf.setInt("mapreduce.job.reduces", partitions)

      val partitionFile = new Path(outputPath,
        TeraInputFormat.PARTITION_FILENAME)
      val jobContext = Job.getInstance(hadoopConf)
      TeraInputFormat.writePartitionFile(jobContext, partitionFile)

      val inputFile = sc.newAPIHadoopFile[Text, Text, TeraInputFormat](inputPath)
      val partitioner =
        new SparkTeraRangePartitioner(
          new TotalOrderPartitioner(hadoopConf, partitionFile),
          partitions
        )
      val repartitioned = inputFile.repartitionAndSortWithinPartitions(partitioner)
      repartitioned.saveAsNewAPIHadoopFile[TeraOutputFormat](outputPath)
    }

    println(time)
  }
}
