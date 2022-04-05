package com.wecreatex.template.infrastructure.database

import com.wecreatex.template.domain.person.Person
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.Macros.createCodecProvider

//todo this should be able to ensure that all domain classes has a codec OR create them implicitly
object DomainMongoCodecs {

  private val personCodec = fromProviders(classOf[Person])

  val allDomainCodecs: CodecRegistry = fromRegistries(
    DEFAULT_CODEC_REGISTRY,
    personCodec
  )

}