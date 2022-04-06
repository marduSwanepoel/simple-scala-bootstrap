package com.wecreatex.template.application

import com.wecreatex.template.domain.address.AddressService
import com.wecreatex.template.domain.person.PeopleService
import com.wecreatex.template.infrastructure.domain.address.AddressAkkaRouter
import com.wecreatex.template.infrastructure.domain.person.PeopleAkkaRouter
import com.wecreatex.utils.httpApi.akka.{AkkaApiRouter, AkkaHttpApi}

/** 
 * Parent [[AkkaHttpApi]] trait that contains all the [[AkkaApiRouter]] instances provided by their infrastructure implementation classes. 
 * 
 * This trait is mixed into the final [[ScalaBootstrapApplicationInstance]] application instance in order to inject (via override) the HTTP API.
 * 
 * */

trait HttpApiInstance extends AkkaHttpApi {

  protected val peopleService: PeopleService
  protected val addressService: AddressService

  private lazy val peopleRouter  = new PeopleAkkaRouter(peopleService)
  private lazy val addressRouter = new AddressAkkaRouter(addressService)

  override protected lazy val routers: List[AkkaApiRouter] = List(
    peopleRouter,
    addressRouter)

}