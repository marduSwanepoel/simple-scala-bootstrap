package com.wecreatex.template.infrastructure.database

import com.wecreatex.template.domain.person.Person
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.Macros.createCodecProvider

object DomainMongoCodecs {

  private val personCodec = fromProviders(classOf[Person])

  val allDomainCodecs: CodecRegistry = fromRegistries(
    DEFAULT_CODEC_REGISTRY,
    personCodec
  )

}