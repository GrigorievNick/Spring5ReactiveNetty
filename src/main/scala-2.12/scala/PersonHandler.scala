package scala

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.{ServerRequest, ServerResponse}
import reactor.core.publisher.{Flux, Mono}

class PersonHandler(val repository: PersonRepository) {
  def getPerson(request: ServerRequest) = {
    val personId: Int = Integer.valueOf(request.pathVariable("id"))
    val person: Mono[Person] = this.repository.getPerson(personId)
    ServerResponse.ok.body(person.doOnNext(println), classOf[Person])
  }

  def createPerson(request: ServerRequest) = {
    val person: Mono[Person] = request.bodyToMono(classOf[Person])
    ServerResponse.ok.build(this.repository.savePerson(person))
  }

  def listPeople(request: ServerRequest) = {
    val people: Flux[Person] = this.repository.allPeople
    ServerResponse
      .ok
      .headers(request.headers.asHttpHeaders)
      .contentType(MediaType.APPLICATION_JSON)
      .body(people, classOf[Person])
  }
}