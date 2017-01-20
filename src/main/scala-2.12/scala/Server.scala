package scala

import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions._
import reactor.ipc.netty.NettyContext
import reactor.ipc.netty.http.server.HttpServer

object Server {
  val HOST: String = "localhost"
  val PORT: Int = 8080

  @throws[Exception]
  def main(args: Array[String]) {
    new Server().startReactorServer()

    println("Press ENTER to exit.")
    scala.io.StdIn.readLine
  }
}

class Server {

  private[scala] def routingFunction: RouterFunction[_] = {
    val repository: PersonRepository = new GameSuggestionRepository
    val handler: GameSugesstionHandler = new GameSugesstionHandler(repository)
    RequestPredicates.GET().
  }

  @throws[InterruptedException]
  private[scala] def startReactorServer() {
    val adapter: ReactorHttpHandlerAdapter = new ReactorHttpHandlerAdapter(toHttpHandler(routingFunction))
    val context: NettyContext = HttpServer.create(Server.HOST, Server.PORT).newHandler(adapter).block()
    context.
  }
}