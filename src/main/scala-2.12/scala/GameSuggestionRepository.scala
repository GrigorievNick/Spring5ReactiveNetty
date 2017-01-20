package scala

import java.util.function.Function

import reactor.core.publisher.{Flux, Mono}

import scala.collection.JavaConverters._
import scala.collection.mutable

class GameSuggestionRepository() extends PersonRepository {

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