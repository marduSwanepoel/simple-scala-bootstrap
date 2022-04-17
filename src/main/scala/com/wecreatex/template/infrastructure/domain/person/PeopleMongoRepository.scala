package com.wecreatex.template.infrastructure.domain.person

import com.wecreatex.template.domain.person.{PeopleRepo, Person}
import com.wecreatex.utils.database.mongodb.MongoCollectionProvider
import com.wecreatex.utils.transport.{Result, ResultA}
import org.mongodb.scala.MongoDatabase
import scala.collection.concurrent.TrieMap

/** Infrastructure-specific implementation of the [[PeopleRepo]]. A Scala in-memory, [[TrieMap]]-based data store implementation in this case. NOTE: just for demo purposes */
class PeopleMongoRepository(implicit db: => Result[MongoDatabase]) extends MongoCollectionProvider[Person] with PeopleRepo {

  override protected val collectionName: String = "people"
  override lazy val database: Result[MongoDatabase] = db

  def insertPerson(person: Person): ResultA[Person] = {
    insert(person)
  }

  def getPerson(id: String): ResultA[Person] = {
    getById(id)
  }

  def deletePerson(id: String): ResultA[Unit] = {
    HARDDeleteById(id)
  }

//  def insertPerson(person: Person): ResultA[Person] = {
//    insert(person)
//  }
//
//  def getPerson(id: String): ResultA[Person] = {
//    getById(id)
//  }
//
//  def deletePerson(id: String): ResultA[Unit] = {
//    HARDDeleteById(id)
//  }

}