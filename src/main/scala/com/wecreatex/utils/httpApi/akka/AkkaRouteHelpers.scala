package com.wecreatex.utils.httpApi.akka

import akka.event.slf4j.SLF4JLogging
import akka.http.scaladsl.server.Directives.{as, entity, provide}
import akka.http.scaladsl.server.{Directive, Directive1, Directives, PathMatcher, Route}
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import com.wecreatex.utils.logging.LoggingUtils

/**
 * Akka-specific HTTP API route utility functions
 */
trait AkkaRouteHelpers extends AkkaRouteCompletion with Directives with LoggingUtils {

  val apiVersion: String
  val baseUrlOpt: Option[String]

  private def apiPath[L](pm: PathMatcher[L]): Directive[L] =
    baseUrlOpt.fold(path(apiVersion / pm))(baseUrl => path(apiVersion / baseUrl / pm))

  private val emptyPathMatcher = ""

  /**
   * Innitializes the construction of a CRUD route
   */
  final def apiDelete[L](pm: PathMatcher[L] = emptyPathMatcher): Directive[L] = apiPath(pm) & delete
  final def apiPatch[L](pm: PathMatcher[L] = emptyPathMatcher): Directive[L]  = apiPath(pm) & patch
  final def apiPost[L](pm: PathMatcher[L] = emptyPathMatcher): Directive[L]   = apiPath(pm) & post
  final def apiPut[L](pm: PathMatcher[L] = emptyPathMatcher): Directive[L]    = apiPath(pm) & put
  final def apiGet[L](pm: PathMatcher[L] = emptyPathMatcher): Directive[L]    = apiPath(pm) & get

  /**
   * Extracts an object from a route
   */
  final def withEntity[M](implicit um: FromRequestUnmarshaller[M]): Directive1[M] = entity(as[M])

  /**
   * Extracts parameters from routes
   */
  final def stringParam(param: String): Directive1[String] = parameter(param.as[String])
  final def boolParam(param: String): Directive1[Boolean]  = parameter(param.as[Boolean])
  final def intParam(param: String): Directive1[Int]       = parameter(param.as[Int])

  final def optStringParam(param: String): Directive1[Option[String]] = parameter(param.as[String].optional)
  final def optBoolParam(param: String): Directive1[Option[Boolean]]  = parameter(param.as[Boolean].optional)
  final def optIntParam(param: String): Directive1[Option[Int]]       = parameter(param.as[Int].optional)

}