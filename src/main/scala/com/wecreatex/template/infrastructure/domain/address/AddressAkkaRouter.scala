package com.wecreatex.template.infrastructure.domain.address

import akka.http.scaladsl.server.Route
import com.wecreatex.template.domain.address.{Address, AddressRouter, AddressService}
import com.wecreatex.utils.httpApi.akka.AkkaApiRouter
import monix.execution.Scheduler

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