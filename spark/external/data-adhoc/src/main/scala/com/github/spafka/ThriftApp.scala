package com.github.spafka

import java.sql.DriverManager

import org.apache.spark.sql.SparkSession

object ThriftApp extends App {

  val spark = SparkSession.builder().appName("thrift").master("local[*]").enableHiveSupport().getOrCreate()

  import spark.sql

  Class.forName("org.apache.hive.jdbc.HiveDriver")
  val conn = DriverManager.getConnection("jdbc:hive2://slave1:10000", "spark", " spark")
  try {
    val statement = conn.createStatement
    statement.executeQuery("use hive")
    val rs = statement.executeQuery("select ordernumber,amount from tbStockDetail where amount>3000")
    while (rs.next) {
      val ordernumber = rs.getString("ordernumber")
      val amount = rs.getString("amount")
      println("ordernumber = %s, amount = %s".format(ordernumber, amount))
    }
  } catch {
    case e: Exception => e.printStackTrace
  }
  conn.close

  spark.stop()
}
