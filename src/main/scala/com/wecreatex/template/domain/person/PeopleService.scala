package com.wecreatex.template.domain.person

import com.wecreatex.utils.transport.ResultA

class PeopleService(repo: PeopleRepo) {

  def createPerson(person: Person): ResultA[Person] ={
    repo.insertPerson(person)
  }

  def getPersonById(id: String): ResultA[Person] = {
    repo.getPerson(id)
  }
  
  def deletePersonById(id: String): ResultA[Unit] = {
    repo.deletePerson(id)
  }

}