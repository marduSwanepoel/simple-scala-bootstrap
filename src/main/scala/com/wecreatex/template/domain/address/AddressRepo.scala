package com.wecreatex.template.domain.address

import com.wecreatex.utils.transport.ResultA

trait AddressRepo {
  
  def insertAddress(address: Address): ResultA[Address]
  
  def getAddressById(addressId: String): ResultA[Address]

}