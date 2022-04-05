package com.wecreatex.template.application

import com.wecreatex.template.infrastructure.database.DomainMongoCodecs
import com.wecreatex.template.infrastructure.domain.person.PeopleMongoRepository
import com.wecreatex.utils.database.mongodb.{MongoCollectionProvider, MongoDatabaseProvider}
import org.bson.codecs.configuration.CodecRegistry

trait MongoCollectionInstances extends MongoDatabaseProvider {

  lazy val peopleRepo: PeopleMongoRepository = new PeopleMongoRepository

  override val domainRegistries: CodecRegistry = DomainMongoCodecs.allDomainCodecs

  override protected lazy val collections: List[MongoCollectionProvider[_]] = List(peopleRepo)

}