package com.wecreatex.template.application

import com.wecreatex.template.domain.address.{AddressRepo, AddressService}
import com.wecreatex.template.domain.person.{PeopleRepo, PeopleService}

/**
 * Contains the domain service instances, provided via the instantiation of their implementations. 
 *
 * This trait is mixed into the final [[ScalaBootstrapApplicationInstance]] application instance in order to inject (via override) the various services.
 * */
trait ServiceInstances {
  
  protected val addressRepo: AddressRepo
  protected val peopleRepo: PeopleRepo

  protected lazy val peopleService  = new PeopleService(peopleRepo)
  protected lazy val addressService = new AddressService(addressRepo)
  
}