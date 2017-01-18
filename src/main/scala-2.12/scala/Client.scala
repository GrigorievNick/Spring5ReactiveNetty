package scala

import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.{ClientRequest, ClientResponse, WebClient}
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

object Client {
  @throws[Exception]
  def main(args: Array[String]) {
    val client: Client = new Client
    client.printAllPeople()
    client.createPerson()
    client.printAllPeople()
  }
}

class Client {
  private val client: WebClient = WebClient.create(new ReactorClientHttpConnector)

  @throws[InterruptedException]
  def printAllPeople() {
    val restTemplate: RestTemplate = new RestTemplate()
    val request: ClientRequest[Void] = ClientRequest.GET("http://{host}:{port}/person",
      Server.HOST.asInstanceOf[AnyRef],
      Server.PORT.asInstanceOf[AnyRef])
      .build

    println(restTemplate.getForEntity(request.url().toASCIIString, classOf[String]))
    val peopleList: Flux[Person] = client.exchange(request)
      .flatMap(resp => resp.bodyToFlux(classOf[Person]))

    StepVerifier.create(peopleList)
      .expectNext(Person("John Doe", 42))
      .expectNext(Person("Jane Doe", 36))
      .expectComplete
      .verify
  }

  def createPerson() {
    val jack: Person = Person("Jack Doe", 16)
    val request: ClientRequest[Person] = ClientRequest.POST("http://{host}:{port}/person",
      Server.HOST.asInstanceOf[AnyRef],
      Server.PORT.asInstanceOf[AnyRef])
      .body(BodyInserters.fromObject(jack))
    val response: Mono[ClientResponse] = client.exchange(request)
    System.out.println(response.block.statusCode)
  }
}