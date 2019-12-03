package com.bgd

import java.io.InputStreamReader
import java.util.Properties

import org.apache.spark.sql.SparkSession


/**
  * @author dengyu
  * @data 2019/12/3 - 15:03
  */
object hiveOnSpark {




  def main(args: Array[String]): Unit = {

    val properties: Properties = PropertiesUtil.load("sql.properties")

    val sql: String = properties.getProperty("sql")

    System.setProperty("HADOOP_USER_NAME","root")

    //this.getClass().getSimpleName().dropRight(1)
    val sparksql: SparkSession = SparkSession.builder().master("local")
      .config("spark.testing.memory", "2147480000")
      .appName("hiveOnSpark")
      .enableHiveSupport()
      .getOrCreate()

    //val sql : String = "create external table ods_user_base (\n    acc_nbr string comment '用户号码',\n    product_type string comment '产品类型',\n    cust_id string comment '客户ID',\n    prd_inst_id string comment '产品实例ID',\n    latn_id  string comment '所在地市',\n    area_id string comment '所在区县'\n)\nrow format delimited fields terminated by '|'\nlines terminated by '\\n'\nstored as textfile location 'hdfs://hdp-1:9000/2019-12-03/ods_user_base'"

    import sparksql.implicits._

    /**
      * sql语句禁止用分号结尾！
      * 在创建外部表的时候要把路径提前创建好建表语句要符合规范
      * 在创建成功会报一个错误
      * Caused by: org.spark_project.guava.util.concurrent.ExecutionError: java.lang.NoClassDefFoundError: org/codehaus/janino/ByteArrayClassLoader
      *
      */
    sparksql.sql(sql).show()

  }


}
