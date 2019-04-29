package com.github.spafka

import java.sql.Date
import java.time.{LocalDateTime, ZoneOffset}
import java.util

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.LocalFileSystem
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Delete, HTable, Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.hadoop.hdfs.DistributedFileSystem
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object delete {


  def main(args: Array[String]): Unit = {


    if (args.length < 2) {
      System.err.print("Error input,plz input 3 param +|" + "startDate[YYYY-MM-DD'T'HH-mm-ss]|" + "endDate[YYYY-MM-DD'T'HH-mm-ss]|" + "tableName[_GPS]")

      System.exit(0)
    }

    // 2017-01-01T00:00:00 2018-01-01T00:00:00 _ABS_2EA|_ABS_311|_BCM_F_392|_BCM_R_393|_EMS_278|_EPB_320|_ICU_431|_EPS_230|_ESP_211|_HVAC_435|_PDC_525|_PEPS_58B|_PEPS_5F5|_PEPS_5F6|_SAS_0C4|_SRS_31D|_TCU_1A0|_TCU_1A1|_TMP_5B0|_TPMS_540
    // ("_ABS_2EA|_ABS_311|_BCM_F_392|_BCM_R_393|_EMS_278|_EPB_320|_ICU_431|_EPS_230|_ESP_211|_HVAC_435|_PDC_525|_PEPS_58B|_PEPS_5F5|_PEPS_5F6|_SAS_0C4|_SRS_31D|_TCU_1A0|_TCU_1A1|_TMP_5B0|_TPMS_540".split("\|")
    val list = args(2).split("\\|")

    val sparkConf = new SparkConf().setAppName("delete").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    list.foreach(xx => {
      val tablename = xx
      val conf: Configuration = HBaseConfiguration.create()
      conf.set(TableInputFormat.INPUT_TABLE, tablename)
      sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE, tablename)
      val scan = new Scan()

      conf.set("fs.hdfs.impl", classOf[DistributedFileSystem].getName)
      conf.set("fs.file.impl", classOf[LocalFileSystem].getName)
      scan.setTimeRange(LocalDateTime.parse(args(0)).toInstant(ZoneOffset.of("+8")).toEpochMilli(), LocalDateTime.parse(args(1)).toInstant(ZoneOffset.of("+8")).toEpochMilli())
      scan.setBatch(10000)
      scan.setMaxResultSize(10000)
      scan.setCacheBlocks(false)
      conf.set(TableInputFormat.SCAN, Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()))

      val job = new Job(sc.hadoopConfiguration)
      job.setOutputKeyClass(classOf[ImmutableBytesWritable])
      job.setOutputValueClass(classOf[Result])
      job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

      val hBaseRDD: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat], classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable], classOf[org.apache.hadoop.hbase.client.Result])

      hBaseRDD.foreachPartition(x => {
        val conf = HBaseConfiguration.create()
        val table = new HTable(conf, tablename)

        //  println(s"process table=${tablename}")
        var deletes: util.List[Delete] = null
        var count: Long = 0L
        deletes = new util.ArrayList[Delete]()
        x.foreach(y => {
          count = count + 1
          deletes.add(new Delete(y._2.getRow()))
          if (deletes.size >= 10000) {
            table.delete(deletes)
            deletes = new util.ArrayList[Delete]()
          }

        })

        if (deletes.size() > 0) {
          try {
            println(s"delete ${tablename} ${new Date(Bytes.toString(deletes.get(0).getRow).split(":")(1).toLong)}")
          } catch {
            case e => {}
          }
        }


        table.delete(deletes)
        print(count)

      })

      Thread.sleep(60000)
    })
    sc.stop()


  }

}


