package com.wecreatex.template.application

import com.wecreatex.template.domain.person.{PeopleRepo, Person}
import com.wecreatex.template.infrastructure.domain.person.PeopleMongoRepository
import com.wecreatex.utils.database.mongodb.{MongoCollectionProvider, MongoDatabaseProvider}

trait MongoCollectionInstances extends MongoDatabaseProvider {

  lazy val peopleRepo: MongoCollectionProvider[Person] & PeopleRepo = new PeopleMongoRepository

  override protected lazy val collections: List[MongoCollectionProvider[_]] = List(peopleRepo)

}