package com.bgd.sparkstreaming

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, KafkaUtils, OffsetRange}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
/**
  * @Author Lucas
  * @Date 2019/12/3 15:03
  * @Version 1.0
  */
object SparkSteamingLogAnalysis {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[3]").setAppName("SparkSteamingLogAnalysis")
    //    val tt = args(0).trim.toLong

    val ssc = new StreamingContext(conf, Seconds(10))
    //    val sc = ssc.sparkContext
    ssc.checkpoint("c:\\aaa")

    val sheData: InputDStream[ConsumerRecord[String, String]] = getKafka(ssc, "topic_start", "groupId")

    val updateFunc = (curVal: Seq[Int], preVal: Option[Int]) => {
      //进行数据统计当前值加上之前的值
      var total = curVal.sum
      //最初的值应该是0
      var previous = preVal.getOrElse(0)
      //Some 代表最终的但会值
      Some(total + previous)
    }
    //获取kafka中的数据
    //处理数据
    sheData.foreachRDD { rdds => {

      val offsetRanges: Array[OffsetRange] = rdds.asInstanceOf[HasOffsetRanges].offsetRanges
      //        print(offsetRanges.length + ", " + offsetRanges.toBuffer)
      //统计结果
      //        val result = offsetRanges(3).ma·p(_._2).flatMap(_.split(" ")).map(word=>(word,1)).updateStateByKey(updateFunc).print()
      // 方法(rdds)
      //         print(rdds.collect().toBuffer)
      sheData.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    }
    }
    val result = sheData.map(_.value()).flatMap(_.split(" ")).map(word=>(word,1)).updateStateByKey(updateFunc).print()

    ssc.start()
    ssc.awaitTermination()
  }


  /**
    * 获取kafka配置信息
    */
  def getKafka(ssc: StreamingContext, topic: String, groupId: String) = {
    val kafkaParams = Map[String, Object](

      "bootstrap.servers" -> "hdp-2:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupId,
      "auto.offset.reset" -> "latest",
      "fetch.max.wait.ms" -> Integer.valueOf(500),
      "enable.auto.commit" -> java.lang.Boolean.valueOf(false)
    )
    val topics = Array(topic)
    val data = KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent,
      Subscribe[String, String](topics, kafkaParams))
    data
  }
}


