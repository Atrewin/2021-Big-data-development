package flink2ML

import java.util
import java.util.{Properties, UUID}

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema
import org.apache.flink.util.Collector

object PredictPositionOfOutlaw {
  val user = "汤欣欣"
  val inputTopics: util.ArrayList[String] = new util.ArrayList[String]() {
    {
      add("mn_buy_ticket_1")
      add("mn_hotel_stay_1")
      add("mn_monitoring_1")
    }
  }
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment


    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val kafkaConsumer = new FlinkKafkaConsumer010[ObjectNode](inputTopics,
      new JSONKeyValueDeserializationSchema(true), kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)
    //inputKafkaStream.filter(x => x.get("value").get("username").asText("").equals(user)).print()
    val sourceStream = inputKafkaStream.filter(x => x.get("value").get("username").asText("").equals(user));

    sourceStream.process(new ProcessFunction[ObjectNode, String] {
      override def processElement(i: ObjectNode, context: ProcessFunction[ObjectNode, String]#Context, collector: Collector[String]): Unit = {
        println(i.get("value"))
        println("预测罪犯接下来可能出现在哪家酒店........")
        val userName:String = i.get("value").get("username").asText()
        val ml = new HttpML()
        ml.post(userName)
      }
    })
    //sourceStream.print()
    //sourceStream.
    env.execute()
  }
}

