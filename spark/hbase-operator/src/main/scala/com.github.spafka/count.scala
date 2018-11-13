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
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object count {

  def main(args: Array[String]): Unit = {


    if (args.length < 2) {
      System.err.print("Error input,plz input 3 param +\n" +
        "startDate[YYYY-MM-DD'T'HH-mm-ss]\n" +
        "endDate[YYYY-MM-DD'T'HH-mm-ss]\n")
      System.exit(0)
    }


    val list = "_ABS_2EA\n_ABS_311\n_BCM_F_392\n_BCM_R_393\n_EMS_278\n_EPB_320\n_ICU_431\n_EPS_230\n_ESP_211\n_HVAC_435\n_PDC_525\n_PEPS_58B\n_PEPS_5F5\n_PEPS_5F6\n_SAS_0C4\n_SRS_31D\n_TCU_1A0\n_TCU_1A1\n_TMP_5B0\n_TPMS_540".split("\\n")
    val sparkConf = new SparkConf().setAppName("bulkload").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    list.foreach(zz => {
      val tablename = zz
      val conf: Configuration = HBaseConfiguration.create()
      conf.set(TableInputFormat.INPUT_TABLE, tablename)

      conf.set("fs.hdfs.impl", classOf[DistributedFileSystem].getName)
      conf.set("fs.file.impl", classOf[LocalFileSystem].getName)
      sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE, tablename)
      val scan = new Scan()

      scan.setTimeRange(LocalDateTime.parse(args(0)).toInstant(ZoneOffset.of("+8")).toEpochMilli(),
        LocalDateTime.parse(args(1)).toInstant(ZoneOffset.of("+8")).toEpochMilli())
      scan.setBatch(10000)
      scan.setMaxResultSize(10000)
      scan.setCacheBlocks(false)
      conf.set(TableInputFormat.SCAN, Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()))

      val job = new Job(sc.hadoopConfiguration)
      job.setOutputKeyClass(classOf[ImmutableBytesWritable])
      job.setOutputValueClass(classOf[Result])
      job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

      val hBaseRDD: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
        classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
        classOf[org.apache.hadoop.hbase.client.Result])

      println(s"$zz=${hBaseRDD.count()}")
    })

    sc.stop()

  }

}
