package com.wecreatex.utils.httpApi

import com.wecreatex.utils.httpApi.akka.AkkaServerConfig
import com.wecreatex.utils.logging.LoggingUtils
import com.wecreatex.utils.transport.Result
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.Scheduler
import org.slf4j.Logger
import scala.concurrent.ExecutionContext

/**
 * Template to be used when creating infrastructure-specific implementations of an HTTP API Server.
 */
private[httpApi] trait HttpApi extends LoggingUtils {

  protected type RoutesType

  protected type ConfigType <: HttpApiConfig

  protected implicit val scheduler: Scheduler = Scheduler.io(
    name = s"http-api-scheduler",
    executionModel = AlwaysAsyncExecution)

  /** Should contain all routers that form part of, and should be bound into, this API. */
  protected lazy val routers: List[HttpApiRouter]

  /** A concatenation of all routers' routes, along with the [[healthRoute]]. */
  protected lazy val allRoutesWithHealth: RoutesType

  /** Requires an implementation to startup the API server by drawing the config from environmental variables */
  def startHttpApiFromEnvironment(): Result[Unit]

  /** Requires an implementation to startup the API server by using a passed-in config */
  def startHttpApiFromConfig(config: ConfigType): Result[Unit]

  /** Requires a health-route implementation that provides a health-status of the service and its internal components */
  protected val healthRoute: RoutesType

}