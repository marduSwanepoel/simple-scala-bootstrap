package com.wecreatex.template.domain.person

import com.wecreatex.utils.time.TimeUtils.nowDate
import com.wecreatex.utils.transport.ResultA
import java.util.UUID
import scala.util.Random

class PeopleService(repo: PeopleRepo) {

  def createPerson(person: Person): ResultA[Person] ={
    repo.insertPerson(person)
  }

  def getPersonById(id: String): ResultA[Person] = {
    repo.getPersonById(id)
  }
  
  def generateRandomPerson: Person = {
    val id      = UUID.randomUUID.toString
    val name    = Random.alphanumeric.take(5).mkString("")
    val surname = Random.alphanumeric.take(10).mkString("")
    Person(id, name, surname, None, nowDate.minusYears(20))
  }

}