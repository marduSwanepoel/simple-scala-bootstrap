package com.wecreatex.template.domain.person

import com.wecreatex.template.domain.address.Address
import com.wecreatex.utils.transport.ResultA

trait PeopleRepo {

  def insertPerson(person: Person): ResultA[Person]

  def getPersonById(id: String): ResultA[Person]

}