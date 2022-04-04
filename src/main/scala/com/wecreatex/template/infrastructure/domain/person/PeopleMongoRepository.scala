package com.wecreatex.template.infrastructure.domain.person

import com.wecreatex.template.domain.person.{PeopleRepo, Person}
import com.wecreatex.utils.database.mongodb.MongoCollectionProvider
import com.wecreatex.utils.transport.ResultA
import com.wecreatex.utils.transport.TransportImplicits.*
import org.mongodb.scala.MongoDatabase

import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.reflect.ClassTag

/** Infrastructure-specific implementation of the [[PeopleRepo]]. A Scala in-memory, [[TrieMap]]-based data store implementation in this case. NOTE: just for demo purposes */
class PeopleMongoRepository(implicit database: MongoDatabase) extends PeopleRepo with MongoCollectionProvider[Person] {

  private type PersonId = String
  override protected lazy val collectionName: String = "people"

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