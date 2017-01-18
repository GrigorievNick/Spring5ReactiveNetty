package scala

import com.lambdaworks.redis.RedisClient


object RedisSupport {
  val client = RedisClient.create("redis://localhost:6379/0")
  val reactive = client.connect().reactive()
  reactive.setAutoFlushCommands(false)
  reactive.get("key")
  reactive.flushCommands()

}
