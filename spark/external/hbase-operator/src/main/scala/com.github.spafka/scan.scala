package com.github.spafka

import java.time.{LocalDateTime, ZoneOffset}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.LocalFileSystem
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.filter.PrefixFilter
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.hadoop.hbase.{Cell, HBaseConfiguration}
import org.apache.hadoop.hdfs.DistributedFileSystem
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object scan {
  def main(args: Array[String]): Unit = {

    //2018-07-17T00:00:00 2018-07-18T00:00:00 _GPS
    if (args.length < 3) {
      System.err.print("Error input,plz input 3 param +\n" + "startDate[YYYY-MM-DD'T'HH-mm-ss]\n" + "endDate[YYYY-MM-DD'T'HH-mm-ss]\n" + "tableName[_GPS]")

      System.exit(0)
    }

    val sparkConf = new SparkConf().setAppName("scan").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    val tablename = args(2)
    val conf: Configuration = HBaseConfiguration.create()
    conf.set(TableInputFormat.INPUT_TABLE, tablename)

    conf.set("fs.hdfs.impl", classOf[DistributedFileSystem].getName)
    conf.set("fs.file.impl", classOf[LocalFileSystem].getName)

    val scan = new Scan()
    scan.setFilter(new PrefixFilter(Bytes.toBytes(new StringBuffer("0b15200196").reverse().toString)))


    scan.setTimeRange(LocalDateTime.parse(args(0)).toInstant(ZoneOffset.of("+8")).toEpochMilli(), LocalDateTime.parse(args(1)).toInstant(ZoneOffset.of("+8")).toEpochMilli())
    scan.setBatch(10000) // 一次scan 返回的数据长度 适用于Mr，默认为1 需建立多次io连接
    scan.setCaching(10000)
    scan.setMaxResultSize(10000)
    scan.setCacheBlocks(false)
    conf.set(TableInputFormat.SCAN, Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()))


    // 直接读取hadoop file文件,设定读取得文件格式
    val hBaseRDD: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat], classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable], classOf[org.apache.hadoop.hbase.client.Result])

    hBaseRDD.foreach { case (_, result) => {

      import scala.collection.JavaConverters._
      val cells: java.util.List[Cell] = result.listCells()
      val cellss = cells.asScala
      cellss.foreach(x => {
        val bytes = x.getQualifier()

        println((Bytes.toString(result.getRow), Bytes.toString(x.getQualifier), Bytes.toString(x.getValue)))
      })


    }
    }

    println(hBaseRDD.count())

    sc.stop()
  }

}
