package com.wecreatex.utils.httpApi.akka

import akka.actor.ActorSystem
import akka.event.slf4j.SLF4JLogging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.scaladsl.{Flow, Sink}
import monix.execution.Scheduler
import org.slf4j.Logger
import akka.actor.ActorSystem
import akka.event.slf4j.SLF4JLogging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Directives.{get, path, *}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.scaladsl.{Flow, Sink}
import com.wecreatex.utils.httpApi.{HttpApi, HttpApiConfig}
import com.wecreatex.utils.logging.LoggingUtils
import com.wecreatex.utils.transport.Result
import monix.execution.Scheduler
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import org.slf4j.Logger
import scala.concurrent.Future
import scala.util.{Failure, Success}
import monix.execution.ExecutionModel.AlwaysAsyncExecution

/** Akka-based [[HttpApi]] implementation */
trait AkkaHttpApi extends HttpApi {

  override type RoutesType = Route
  override type ConfigType = AkkaServerConfig

  lazy val routers: List[AkkaApiRouter]

  protected lazy val allRoutesWithHealth: Route = routers.foldLeft(healthRoute){ case (routes, router) =>  routes ~ router.routes }

  override def startFromEnvironment(): Result[Unit] = {
    AkkaServerConfig
      .loadFromEnv
      .flatMap(startFromConfig)
  }

  override def startFromConfig(config: AkkaServerConfig): Result[Unit] = {
    Result.attemptUnsafe(startUnsafe(config))
  }

  private def startUnsafe(config: AkkaServerConfig): Unit =
    configureAndRun(allRoutesWithHealth, config.host, config.port, config.parallelism, ServerSettings(config.system))(config.system, config.scheduler)

  private def configureAndRun(routeIn: Route, host: String, port: Int, parallelism: Int = 8, settings: ServerSettings)(implicit actSys: ActorSystem, scheduler: Scheduler): Unit = {
    val sink   = Sink.foreachAsync(parallelism) { (conn: Http.IncomingConnection) =>
      Future.apply {
        conn.handleWith(Flow[HttpRequest].mapAsyncUnordered(parallelism)(Route.toFunction(routeIn))); ()
      }
    }

    Http()
      .newServerAt(interface = host, port)
      .connectionSource()
      .to(sink)
      .run()
      .onComplete {
        case Success(binding) => logInfo(s"Service listening on: ${binding.localAddress.getAddress}:${binding.localAddress.getPort}", "HTTP Connection status")
        case Failure(err)     => logError(s"Failure binding on port $port with message '${err.getMessage}'", "HTTP Connection status")
      }
  }

  //TODO: finish health check
  protected val healthRoute: Route = get {
    path("health") {
      complete(StatusCodes.OK, "service is up")
    }
  }

}