package com.github.spafka

import java.time.{LocalDateTime, ZoneOffset}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.LocalFileSystem
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.Base64
import org.apache.hadoop.hdfs.DistributedFileSystem
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object HbaseSql {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("bulkload").setMaster("local[*]")

    val spark = SparkSession.builder.appName("Spark Hbase").master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    val tablename = "_GPS"

    val conf: Configuration = HBaseConfiguration.create()
    conf.set(TableInputFormat.INPUT_TABLE, tablename)

    conf.set("fs.hdfs.impl", classOf[DistributedFileSystem].getName)
    conf.set("fs.file.impl", classOf[LocalFileSystem].getName)
    sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE, tablename)
    val scan = new Scan()

    scan.setTimeRange(LocalDateTime.parse(args(0)).toInstant(ZoneOffset.of("+8")).toEpochMilli(), LocalDateTime.parse(args(1)).toInstant(ZoneOffset.of("+8")).toEpochMilli())
    scan.setBatch(10000)
    scan.setMaxResultSize(10000)
    scan.setCacheBlocks(false)
    conf.set(TableInputFormat.SCAN, Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()))
    val hBaseRDD: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat], classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable], classOf[org.apache.hadoop.hbase.client.Result])
    val hbfrdd = hBaseRDD.map { case (_, result) => {
      import org.apache.hadoop.hbase.util.Bytes
      val lat: String = Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("lat")))
      val lng: String = Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("lng")))
      val gpsok: String = Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("gpsok")))
      (lat, lng, gpsok)
    }
    }

    import spark.implicits._

    val hbfrddDF = hbfrdd.toDF("lat", "lng", "gpsok")
    hbfrddDF.registerTempTable("hbf")
    val sqlcommand = "select * from  hbf"

    spark.sql(sqlcommand).show()

    spark.close()
  }
}
