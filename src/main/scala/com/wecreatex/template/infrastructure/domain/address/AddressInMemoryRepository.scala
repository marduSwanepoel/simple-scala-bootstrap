package com.wecreatex.template.infrastructure.domain.address

import com.wecreatex.template.domain.address.{Address, AddressRepo}
import com.wecreatex.template.domain.person.{PeopleRepo, Person}
import com.wecreatex.utils.transport.ResultA
import com.wecreatex.utils.transport.TransportImplicits._
import scala.collection.concurrent.TrieMap
import scala.collection.mutable

/** Infrastructure-specific implementation of the [[AddressRepo]]. A Scala in-memory, [[TrieMap]]-based data store implementation in this case. NOTE: just for demo purposes */
class AddressInMemoryRepository extends AddressRepo {
  
  private type AddressId = String
  
  private val inMemoryMap: mutable.Map[AddressId, Address] = TrieMap.empty[AddressId, Address]

  override def insertAddress(address: Address): ResultA[Address] = {
    inMemoryMap.addOne(address.id, address)
    ResultA.right(address)
  }

  override def getAddressById(addressId: AddressId): ResultA[Address] = {
    inMemoryMap
      .get(addressId)
      .liftA(s"Address with id $addressId not found")
  }
  
}