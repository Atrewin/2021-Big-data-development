import java.util.{Properties, UUID}

import com.alibaba.fastjson.JSON
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.{SlidingProcessingTimeWindows, TumblingProcessingTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.util.Collector
import org.apache.flink.streaming.api.scala.function.ProcessAllWindowFunction

object top5City {
  /**
   * 输入的主题名称
   */
  val inputTopic = "mn_buy_ticket_1_jinhui"
  /**
   * kafka地址
   */
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(2)
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
    val stream1 = inputKafkaStream.map(x => JSON.parseObject(x))
    stream1.map(x=>(x.getString("destination"),1L))
      .keyBy(0)
      .window(SlidingProcessingTimeWindows.of(Time.seconds(60L), Time.seconds(10L)))
      .sum(1)
      .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10L)))
      .process(new ProcessAllWindowFunction[(String, Long), String, TimeWindow] {
        override def process(context: Context, elements: Iterable[(String, Long)], out: Collector[String]): Unit = {
          val top5 = elements.toSeq
            .sortBy(-_._2)
            .take(5)
            .zipWithIndex
            .map { case ((dest, times), idx) => s"   ${idx + 1}. $dest: $times" }
            .mkString("\n")
          out.collect(("-" * 16) + "\n" + top5)
        }
      })
      .print()
    env.execute()
  }
}