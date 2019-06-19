import org.apache.spark.sql.SparkSession

object CsvApp {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("Csv").master("local[*]").getOrCreate()

    import org.apache.commons.lang3.StringUtils
    import spark.implicits._

    val sc = spark.sparkContext
    //会根据值自动生成类型
    val csvdf = spark.read.format("csv").option("sep", ",").option("inferSchema", "true").option("header", "true").option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ").load("spark\\src\\main\\scala\\21.csv")
    csvdf.createOrReplaceTempView("csv")

    val jdbcDF11 = spark.read.format("jdbc").option("driver", "com.mysql.jdbc.Driver").option("url", "jdbc:mysql://192.168.30.102:3306/dms4ht").option("dbtable", "t_esales").option("user", "root").option("password", "kevin115") //.option("fetchsize", "3")
      .load()

    jdbcDF11.createOrReplaceTempView("t_esales")

    spark.sql("select a.id, a.FIRSTNAME,b.firstname ,a.CERTIFICATECODE,b.certificatecode from  t_esales a inner join  csv b on a.FIRSTNAME =b.firstname where a.LOGINNAME !='0' ").map(x ⇒ (
      x.getAs[Int](0), x.getAs[String](1), x.getAs[Long](2), x.getAs[String](3), x.getAs[String](4)
    )).filter(x ⇒ x._5 != null && StringUtils.isNoneBlank(x._4) && StringUtils.isNoneBlank(x._4)).filter(x ⇒ !x._4.equals(x._5)).foreach(println(_))

    spark.stop()
  }

}
