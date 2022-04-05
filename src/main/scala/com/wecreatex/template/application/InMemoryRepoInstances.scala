package com.wecreatex.template.application

import com.wecreatex.template.domain.address.{AddressRepo, AddressService}
import com.wecreatex.template.domain.person.{PeopleRepo, PeopleService}
import com.wecreatex.template.infrastructure.domain.address.AddressInMemoryRepository
import com.wecreatex.template.infrastructure.domain.person.PeopleInMemoryRepository

/**
 * Contains the repository instances, provided via the instantiation of their infrastructure-specific implementations. 
 * 
 * In this case, the repositories are implemented as InMemory memory stores. NOTE: this is only for demo purposes.
 * 
 * This trait is mixed into the final [[ScalaBootstrapApplicationInstance]] application instance in order to inject (via override) the various repositories.
 * */
trait InMemoryRepoInstances {

  lazy val addressRepo: AddressRepo = new AddressInMemoryRepository()
//  lazy val peopleRepo: PeopleRepo = new PeopleInMemoryRepository

}