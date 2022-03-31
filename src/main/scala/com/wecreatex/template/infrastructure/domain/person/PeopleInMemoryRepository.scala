package com.wecreatex.template.infrastructure.domain.person

import com.wecreatex.template.domain.person.{PeopleRepo, Person}
import com.wecreatex.utils.transport.ResultA
import com.wecreatex.utils.transport.TransportImplicits._
import scala.collection.concurrent.TrieMap
import scala.collection.mutable

/** Infrastructure-specific implementation of the [[PeopleRepo]]. A Scala in-memory, [[TrieMap]]-based data store implementation in this case. NOTE: just for demo purposes */
class PeopleInMemoryRepository extends PeopleRepo {

  private type PersonId = String

  private val inMemoryMap: mutable.Map[PersonId, Person] = TrieMap.empty[PersonId, Person]

  def insertPerson(person: Person): ResultA[Person] = {
    inMemoryMap.addOne(person.id, person)
    ResultA.right(person)
  }

  def getPersonById(id: String): ResultA[Person] = {
    inMemoryMap
      .get(id)
      .liftA(s"Person with id $id not found")
  }

}