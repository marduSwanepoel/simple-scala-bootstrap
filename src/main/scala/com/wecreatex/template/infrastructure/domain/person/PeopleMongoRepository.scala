package com.wecreatex.template.infrastructure.domain.person

import com.wecreatex.template.domain.person.{PeopleRepo, Person}
import com.wecreatex.utils.database.mongodb.MongoCollectionProvider
import com.wecreatex.utils.transport.{Result, ResultA}
import com.wecreatex.utils.transport.TransportImplicits._
import org.mongodb.scala.MongoDatabase
import scala.collection.concurrent.TrieMap
import scala.collection.mutable

/** Infrastructure-specific implementation of the [[PeopleRepo]]. A Scala in-memory, [[TrieMap]]-based data store implementation in this case. NOTE: just for demo purposes */
class PeopleMongoRepository(implicit val database: Result[MongoDatabase]) extends MongoCollectionProvider[Person] with PeopleRepo {

  private type PersonId = String
  override protected val collectionName: String = "people2"

  private val inMemoryMap: mutable.Map[PersonId, Person] = TrieMap.empty[PersonId, Person]

  def insertPerson(person: Person): ResultA[Person] = {
    insert(person)
  }

  def getPersonById(id: String): ResultA[Person] = {
    inMemoryMap
      .get(id)
      .liftA(s"Person with id $id not found")
  }

}