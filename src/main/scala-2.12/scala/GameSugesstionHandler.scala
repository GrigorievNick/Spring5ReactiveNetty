package scala

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.{ServerRequest, ServerResponse}
import reactor.core.publisher.Mono

class GameSugesstionHandler(val repository: PersonRepository) {

  def getSuggestion(request: ServerRequest) = {
    val personId: Int = Integer.valueOf(request.pathVariable("id"))
    val person: Mono[Person] = this.repository.getPerson(personId)
    ServerResponse
      .ok
      .contentType(MediaType.APPLICATION_JSON)
      .body(person, classOf[Person])
  }
}