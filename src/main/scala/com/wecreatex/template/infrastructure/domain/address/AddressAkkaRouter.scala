package com.wecreatex.template.infrastructure.domain.address

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.scaladsl.{Flow, Sink}
import com.wecreatex.template.domain.address.{Address, AddressRouter, AddressService}
import com.wecreatex.template.domain.person.{PeopleService, Person}
import com.wecreatex.utils.httpApi.akka.AkkaApiRouter
import com.wecreatex.utils.time.TimeUtils.nowDate
import monix.execution.Scheduler
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

/** Infrastructure-specific implementation of the [[AddressRouter]], implemented with AKKA HTTP */
class AddressAkkaRouter(service: AddressService)(implicit val scheduler: Scheduler) extends AddressRouter with AkkaApiRouter with AddressV1Formats {

  override type RouteType = Route
  override val apiVersion: String = "v1"
  override val baseUrlOpt = Some("addresses")
  override lazy val routes: Route = getAddress ~ postAddress

  def getAddress: Route = apiGet(Segment) { addressId =>
    service
      .getAddressById(addressId)
      .completeWithResponse
  }

  def postAddress: Route = (apiPost()
    & withEntity[Address]) { address =>
    service
      .createAddress(address)
      .completeWithResponse
  }

}