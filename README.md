# bgd电影用户统计项目
一、数据来源及其格式
离线数据的格式：
userId	movieId	rating	timestamp
1	2	3.5	1112486027
1	29	3.5	1112484676

二、数据读取
Flunme:读取数据将数据下沉到kafka中
Flume的配置文件
a1.sources = source1
a1.sinks = k1
a1.channels = c1
a1.sources.source1.type = exec
a1.sources.source1.command = tail -F /root/bigdata.txt

a1.sinks.k1.type = org.apache.flume.sink.kafka.KafkaSink
a1.sinks.k1.topic = bigdata
a1.sinks.k1.brokerList = hdp-1:9092,hdp-2:9092,hdp-3:9092
a1.sinks.k1.requiredAcks = 1
a1.sinks.k1.batchSize = 20

a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

a1.sources.source1.channels = c1
a1.sinks.k1.channel = c1

启动flume
$FLUME_HOME/bin/flume-ng agent \
-c conf \
-n a1 \
-f $FLUME_HOME/conf/bigdata.properties \
-Dflume.root.logger=DEBUG,console


三、数据中间件(kafka)
用来缓存数据
启动kafka消费者
./kafka-console-consumer.sh \
--bootstrap-server hdp-1:9092  \
--from-beginning \
--topic bigdata


四、数据处理（SparkStreaming）
代码已上传
五、数据储存

六、数据展示
