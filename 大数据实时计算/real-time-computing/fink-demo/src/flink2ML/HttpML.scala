package flink2ML

import com.google.gson.Gson
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils

class HttpML {
  val client = new DefaultHttpClient
  val SUCCESS_CODE: Int = 200
  //val url:String = "http://dig.projects.bingosoft.net:8083/daasModel/transferApi?Action=CallModelOnline"
  val url:String = "http://dig.projects.bingosoft.net:8084/daasModel/transferApi/Invoke"

  def main(args: Array[String]): Unit = {
    post("李淳美")
  }

  def post(user:String) = {

    val post = new HttpPost(url)
    val entity = new RequestEntity(user)
    val gson = new Gson()
    val entityStr:String = gson.toJson(entity)
    println(entityStr);
    val stringEntity = new StringEntity(entityStr, "UTF-8")
    stringEntity.setContentEncoding("UTF-8")
    stringEntity.setContentType("application/json")
    post.setEntity(stringEntity)
    //post.setHeader(new BasicHeader("Content-Type", ))
    //post.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"))
    //post.setHeader(new BasicHeader("Accept", "text/plain;charset=utf-8"))
    val response = client.execute(post)
    val statusCode = response.getStatusLine.getStatusCode
    if (SUCCESS_CODE == statusCode) {
      val result = EntityUtils.toString(response.getEntity, "UTF-8")
      println("可能出现在以下酒店：")
      println(result)
    } else {
      println(statusCode)
      val result = EntityUtils.toString(response.getEntity, "UTF-8")
      println(result)
    }
  }

}
