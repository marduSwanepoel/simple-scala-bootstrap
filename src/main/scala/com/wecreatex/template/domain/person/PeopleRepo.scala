package com.wecreatex.template.domain.person

import com.wecreatex.utils.transport.ResultA

trait PeopleRepo {

  def insertPerson(person: Person): ResultA[Person]

  def getPerson(id: String): ResultA[Person]

  def deletePerson(id: String): ResultA[Unit]

}