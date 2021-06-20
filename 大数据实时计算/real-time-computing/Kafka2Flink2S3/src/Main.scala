import java.util.{Properties, UUID}

import S3Reader.{produceToKafka, readFile}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010

object Main {
  val accessKey = "3C792CB222BEAAE59195"
  val secretKey = "WzhEMEI2RUVDNDY0MjE0MDM1REQxRjJDNzExODE4"

  //s3地址
  val endpoint = "scuts3.depts.bingosoft.net:29997"
  //上传到的桶
  val bucket = "jinhui"
  //上传文件的路径前缀
  val keyPrefix = "upload/"
  val key = "buy_ticket_record.txt"
  //上传数据间隔 单位毫秒
  val period = 30000
  //输入的kafka主题名称
  val inputTopic = "mn_buy_ticket_1_jinhui"

  //kafka地址
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  val filename = "daas"+System.nanoTime() + ".txt"

  S3Reader.accessKey = accessKey
  S3Reader.secretKey = secretKey
  S3Reader.endpoint = endpoint
  S3Reader.bucket = bucket
  S3Reader.key = key
  S3Reader.topic = inputTopic
  S3Reader.bootstrapServers = bootstrapServers

  def main(args: Array[String]): Unit = {

    // s3 to kafka
    val s3Content = readFile()
    produceToKafka(s3Content)

    // kafka producer to flink
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(2)//并行度
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val kafkaConsumer = new FlinkKafkaConsumer010[String](inputTopic,
      new SimpleStringSchema, kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)//设置数据源

    // flink to s3
    // Transformation of data in S3Writer Class 订阅模式 inputKafkaStream已经是flink 的数据计算句柄
    // 消费者是直接写入到S3这样的消费
    //transfer



    //sink
    inputKafkaStream.writeUsingOutputFormat(new S3Writer(accessKey, secretKey, endpoint, bucket, keyPrefix, period, filename))

    //启动流处理
    env.execute()
  }
}
