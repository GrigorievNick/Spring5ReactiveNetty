package scala

import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.server.reactive.{HttpHandler, ReactorHttpHandlerAdapter}
import org.springframework.web.reactive.function.server.RequestPredicates._
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions._
import reactor.core.publisher.Mono
import reactor.ipc.netty.NettyContext
import reactor.ipc.netty.http.server.HttpServer

object Server {
  val HOST: String = "localhost"
  val PORT: Int = 8080

  @throws[Exception]
  def main(args: Array[String]) {
    val JServer: Server = new Server
    JServer.startReactorServer()

    println("Press ENTER to exit.")
    scala.io.StdIn.readLine
  }
}

class Server {

  private[scala] def routingFunction: RouterFunction[_] = {
    val repository: PersonRepository = new DummyPersonRepository
    val handler: PersonHandler = new PersonHandler(repository)
    route(GET("/person/{id}").and(accept(MediaType.APPLICATION_JSON)), handler.getPerson)
      .and(route(GET("/person").and(accept(APPLICATION_JSON)), handler.listPeople))
      .and(route(POST("/person").and(contentType(APPLICATION_JSON)), handler.createPerson))
  }

  @throws[InterruptedException]
  private[scala] def startReactorServer() {
    val adapter: ReactorHttpHandlerAdapter = new ReactorHttpHandlerAdapter(toHttpHandler(routingFunction))
    val server: HttpServer = HttpServer.create(Server.HOST, Server.PORT)
    val handler: Mono[_ <: NettyContext] = server.newHandler(adapter)
    handler.block()

  }
}