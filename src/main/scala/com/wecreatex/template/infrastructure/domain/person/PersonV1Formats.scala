package com.wecreatex.template.infrastructure.domain.person

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.wecreatex.template.domain.person.Person
import com.wecreatex.utils.json.spray.DefaultJsonFormats
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/** 
 * JSON formats required for the serialization and deserialization of the [[Person]] domain entity. These formats are versioned 
 * according to the API release-versions.
 * */
trait PersonV1Formats  extends SprayJsonSupport with DefaultJsonProtocol with DefaultJsonFormats {
  implicit val personFmt: RootJsonFormat[Person] = jsonFormat5(Person.apply)
}
