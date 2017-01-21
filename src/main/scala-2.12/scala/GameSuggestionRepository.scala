package scala

import java.util.function.Function

import com.lambdaworks.redis.api.reactive.RedisReactiveCommands
import com.lambdaworks.redis.api.{StatefulConnection, StatefulRedisConnection}
import com.lambdaworks.redis.support.ConnectionPoolSupport
import com.lambdaworks.redis.{RedisClient, RedisURI, TransactionResult}
import org.apache.commons.pool2.impl.{GenericObjectPool, GenericObjectPoolConfig}
import reactor.core.publisher.{Flux, Mono}

import scala.collection.JavaConverters._
import scala.collection.mutable

class GameSuggestionRepository() extends PersonRepository {

  val client = RedisClient.create(RedisURI.create("localhost", 89))
  val pool: GenericObjectPool[StatefulRedisConnection[String, String]] =
    ConnectionPoolSupport.createGenericObjectPool(() => client.connect(), new GenericObjectPoolConfig())

  // executing work
  private val borrowObject: StatefulRedisConnection[String, String] = pool.borrowObject()
  private val reactive: RedisReactiveCommands[String, String] = borrowObject.reactive()

  private val flux: Flux[TransactionResult] = reactive.multi()
    .doOnSuccess(s => reactive.lrange("key", 1, 10).doOnNext(s1 => System.out.println(s1)).subscribe())
    .flatMap(s => reactive.exec())
  flux
    .doOnNext(transactionResults => println(transactionResults.wasRolledBack()))
    .flatMap(result => Flux.fromStream(result.stream()))

  // terminating
  pool.close()
  client.shutdown()

  private val people: mutable.MutableList[Person] =
    mutable.MutableList(
      Person("John Doe", 42),
      Person("Jane Doe", 36)
    )

  def getPerson(id: Int): Mono[Person] = Mono.justOrEmpty(people(id))

  def allPeople: Flux[Person] = Flux.fromIterable(people.asJava)

  def savePerson(personMono: Mono[Person]): Mono[Void] = {
    val personToMono: Function[Person, Mono[Void]] = person => {
      people += person
      printf("Saved %s with id %d%n", person, people.size)
      Mono.empty[Void]()
    }
    personMono.then(personToMono)
  }
}