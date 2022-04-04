package com.wecreatex.utils.httpApi

import monix.execution.Scheduler
import scala.concurrent.ExecutionContext

/**
 * Template to be used when creating infrastructure-specific implementations of an HTTP API Router.
 */
private[httpApi] trait HttpApiRouter {

  type RoutesType

  lazy val routes: RoutesType

  /** The version of the specific router within the API, to be prepended to all routes in the format
   * {{{
   * www.myserver.com/<PREFIX>/<apiVersion>/...
   * }}} */
  val apiVersion: String

  /** Default base URL appended to all routes within this router, in the format 
   * {{{
   *   www.myserver.com/<PREFIX>/<apiVersion>/<baseUrlOpt>/...
   * }}} */
  val baseUrlOpt: Option[String] = None

  protected implicit val scheduler: Scheduler

}