package extendWork

import org.apache.flink.streaming.api.scala._
case class SensorReading(name: String, timestamp: Long, salary: Double)

object flink2Sql {
  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val dataStream: DataStream[String] = env.readTextFile("data.txt")

    val stream = dataStream.map(data => {
      val splited = data.split(",")
      SensorReading(splited(0), splited(1).trim.toLong, splited(2).trim.toDouble)
    })
    stream.addSink(new sqlWriter())


    env.execute("job")
  }
}