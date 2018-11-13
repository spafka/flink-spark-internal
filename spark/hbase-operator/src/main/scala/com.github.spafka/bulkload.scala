package com.github.spafka

import java.time.{LocalDateTime, ZoneOffset}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.LocalFileSystem
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Put, Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.hadoop.hdfs.DistributedFileSystem
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters._

object bulkread {


  def main(args: Array[String]): Unit = {


    if (args.length < 3) {
      System.err.print("Error input,plz input 3 param +\n" +
        "startDate[YYYY-MM-DD'T'HH-mm-ss]\n" +
        "endDate[YYYY-MM-DD'T'HH-mm-ss]\n" +
        "tableName[_GPS]")

      System.exit(0)
    }

    val sparkConf = new SparkConf().setAppName("bulkload").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    val tablename = args(2)
    val conf: Configuration = HBaseConfiguration.create()
    conf.set(TableInputFormat.INPUT_TABLE, tablename)

    conf.set("fs.hdfs.impl", classOf[DistributedFileSystem].getName)
    conf.set("fs.file.impl", classOf[LocalFileSystem].getName)

    val scan = new Scan()

    scan.setTimeRange(LocalDateTime.parse(args(0)).toInstant(ZoneOffset.of("+8")).toEpochMilli(),
      LocalDateTime.parse(args(1)).toInstant(ZoneOffset.of("+8")).toEpochMilli())
    scan.setBatch(10000)
    // 一次scan 返回的数据长度 适用于Mr，默认为1 需建立多次io连接
    scan.setCaching(10000)
    scan.setMaxResultSize(10000)
    scan.setCacheBlocks(false)
    conf.set(TableInputFormat.SCAN, Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()))


    // 直接读取hadoop file文件,设定读取得文件格式
    val hBaseRDD: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])

    // 存的格式
    sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE, tablename)
    val job = new Job(sc.hadoopConfiguration)
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setOutputValueClass(classOf[Put]) //好像不是必须的
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

    val rdd: RDD[(ImmutableBytesWritable, Put)] = hBaseRDD.map(x => {

      val oldKey = Bytes.toString(x._1.get())

      val strings = oldKey.split(":")
      val newKey = strings(0)
      val put = new Put(Bytes.toBytes(newKey))
      x._2.listCells().asScala.foreach(y => {
        put.add(y.getFamily, y.getQualifier, y.getValue)
      })
      (new ImmutableBytesWritable(), put)
    })

    rdd.saveAsNewAPIHadoopDataset(job.getConfiguration)

    // rdd.saveAsNewAPIHadoopFile()
    sc.stop()

  }
}


