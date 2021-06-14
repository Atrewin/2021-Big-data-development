import java.util.Properties
import scala.util.control._
import java.sql.DriverManager

class SparkHandler(hostText: String, portText: String, databaseText: String, usernameText: String, passwordText: String) {
  var host:String = hostText
  var port:String = portText
  var database:String = databaseText
  var username:String = usernameText
  var password:String = passwordText

  val loop = new Breaks;

  val url = "jdbc:hive2://"+host+":"+port+"/"+database
  val properties = new Properties()
  properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver")
  properties.setProperty("user", username)
  properties.setProperty("password", password)

  val connection = DriverManager.getConnection(url, properties)

  var statement = connection.createStatement

  def getTables(): Array[String] = {
    var tables = new Array[String](0)
    val queryTable = statement.executeQuery("show tables")
    try {
      while (queryTable.next) {
        val tableName = queryTable.getString(1)
        tables=tables:+tableName
      }

    }catch {
      case e: Exception => e.printStackTrace()
    }
    tables
  }

  def getColumn(table:String): Array[String] = {
    var columns = new Array[String](0)
    val resultSet = statement.executeQuery("show columns from "+table)
    try {
      while (resultSet.next) {
        val column = resultSet.getString(1)
        //输出所有表名
        columns=columns:+column
      }
    }catch {
      case e: Exception => e.printStackTrace()
    }
    columns
  }

  def getRes(query:String): Array[Array[String]] = {
    var resultSet = statement.executeQuery(query)
    val data = resultSet.getMetaData();
    val colCount = data.getColumnCount()
    var rowCount = 0
    while(resultSet.next) {
      rowCount+=1;
    }
    resultSet.close()
    resultSet = statement.executeQuery(query)
    val res= Array.ofDim[String](rowCount+1,colCount)
    for( i <- 0 to colCount-1){
      res(0)(i) = data.getColumnName(i+1)
    }
    var tag=1
    while (resultSet.next) {
      for( i <- 0 to colCount-1){
        res(tag)(i) = resultSet.getString(i+1)
      }
      tag+=1
    }
    res
  }
}
