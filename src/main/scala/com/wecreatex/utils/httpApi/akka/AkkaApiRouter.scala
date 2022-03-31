package com.wecreatex.utils.httpApi.akka

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.scaladsl.{Flow, Sink}
import com.wecreatex.utils.httpApi.HttpApiRouter
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.Scheduler
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/** Akka-based [[HttpApiRouter]] implementation, along with [[AkkaRouteCompletion]] and [[AkkaRouteHelpers]] utilities */
trait AkkaApiRouter
  extends HttpApiRouter
    with AkkaRouteCompletion
    with AkkaRouteHelpers {

  override type RoutesType = Route

}