package com.wecreatex.template.domain.address

import com.wecreatex.utils.time.TimeUtils.nowDate
import com.wecreatex.utils.transport.ResultA
import scala.util.Random

class AddressService(repo: AddressRepo) {
  
  def createAddress(address: Address): ResultA[Address] = {
    repo.insertAddress(address)
  }
  
  def getAddressById(id: String): ResultA[Address] = {
    repo.getAddressById(id)
  }

}