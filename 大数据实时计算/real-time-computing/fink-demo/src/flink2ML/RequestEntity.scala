package flink2ML

class RequestEntity(user:String) {
  private val modelId = "7ed17463-a49c-4a39-b498-98e02e385c6f"
  private val version = 1
  private val invocationType = "synchronous"
  private val userId = "bingocc"
  private val event = new InputEntity(user)
}
