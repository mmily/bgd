package com.bgd.sparkstreaming

/**
  * author: star
  * time: 2019.12.3
  * 说明：该class是为了充当kafka的消费者来消费数据，根据我们想要的格式改变其数据
  *       程序没写完
  */

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.immutable
object Kafka_SM {
  //TODO 有状态数据统计
    def main(args: Array[String]): Unit = {
      //使用SparkStreaming完成聚合
      //Spark配置对象
      val sparkConf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("Kafka_SM")
      //实时数据分析配置对象
      //采集周期：以指定的时间为周期采集实时数据
      val streamingContext = new StreamingContext(sparkConf,Seconds(5))
      //保存数据的状态，需要设定检查点路径
      streamingContext.sparkContext.setCheckpointDir("cp")
      //从kafka中采集数据
      val kafkaDStream: ReceiverInputDStream[(String, String)] = KafkaUtils.createStream(
        streamingContext,
        "hdp-1:2181",
        "bigdata",
        Map("bigdata" -> 3)
      )
      //将我们采集的数据进行分解（扁平化）
      val wordDStream: DStream[String] = kafkaDStream.flatMap(
        t=>t._2.split("\t")
      )

      //将数据进行结构的转换方便统计分析
      val mapDStream: DStream[(String, Int)] = wordDStream.map((_,1))

      //将转换结构后的数据进行聚合处理
      //val wordToSumDStream: DStream[(String, Int)] = mapDStream.reduceByKey(_+_)
      val stateDStream: DStream[(String, Int)] = mapDStream.updateStateByKey {
        case (seq, buffer) => {
          val sum = buffer.getOrElse(0) + seq.sum
          Option(sum)
        }
      }
      //将结果打印出来
      stateDStream.saveAsTextFiles("out/out",".log")
      //不能停止采集程序，所以不要加stop
      //启动采集器
      streamingContext.start()
      //Drvier等待采集器的执行
      streamingContext.awaitTermination()
    }
}
