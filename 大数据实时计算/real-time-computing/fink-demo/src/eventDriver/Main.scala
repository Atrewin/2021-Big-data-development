package eventDriver

import java.util.{Properties, UUID}

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010

object Main {
  val accessKey = "3C792CB222BEAAE59195"
  val secretKey = "WzhEMEI2RUVDNDY0MjE0MDM1REQxRjJDNzExODE4"
  //s3地址
  val endpoint = "10.16.0.1:81"
  //上传到的桶
  val bucket = "jinhui"
  //上传文件的路径前缀
  val keyPrefix = "storage/"
  //上传数据间隔 单位毫秒
  val period = 5000
  //输入的kafka主题名称
  val inputTopic = "mn_buy_ticket_1"
  //kafka地址
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val kafkaConsumer = new FlinkKafkaConsumer010[String](inputTopic,
      new SimpleStringSchema, kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)
    inputKafkaStream.writeUsingOutputFormat(new S3CsvWriter(accessKey, secretKey, endpoint, bucket, keyPrefix, period))
    env.execute()
  }
}
