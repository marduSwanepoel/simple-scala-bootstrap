package com.wecreatex.utils.httpApi.akka

import akka.http.scaladsl.server.Route
import com.wecreatex.utils.httpApi.HttpApiRouter

/** Akka-based [[HttpApiRouter]] implementation, along with [[AkkaRouteCompletion]] and [[AkkaRouteHelpers]] utilities */
trait AkkaApiRouter
  extends HttpApiRouter
    with AkkaRouteCompletion
    with AkkaRouteHelpers {

  override type RoutesType = Route

}