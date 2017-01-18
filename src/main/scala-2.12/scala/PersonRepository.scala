package scala

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

trait PersonRepository {
  def getPerson(id: Int): Mono[Person]

  def allPeople: Flux[Person]

  def savePerson(person: Mono[Person]): Mono[Void]
}