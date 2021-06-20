package extendWork

import java.sql.{Connection, DriverManager, PreparedStatement}
import java.util.Properties

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

class sqlWriter() extends RichSinkFunction[SensorReading]{
  // 定义数据库连接、预编译器
  var conn: Connection = _
  var insertStmt: PreparedStatement = _
  var updateStmt: PreparedStatement = _
  // 初始化，创建连接和预编译语句
  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    val url = "jdbc:hive2://bigdata115.depts.bingosoft.net:22115/user16_db"
    val properties = new Properties()
    properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver")
    properties.setProperty("user", "user16")
    properties.setProperty("password", "pass@bingo16")
    conn = DriverManager.getConnection(url, properties)
    insertStmt = conn.prepareStatement("INSERT INTO salary_table (name, salary) VALUES (?,?)")
  }
  // 调用连接，执行sql
  override def invoke(value: SensorReading, context: SinkFunction.Context[_]): Unit = {
    insertStmt.setString(1, value.name)
    insertStmt.setDouble(2, value.salary)
    insertStmt.execute()
  }
  // 关闭时做清理工作
  override def close(): Unit = {
    insertStmt.close()
    conn.close()
  }
}