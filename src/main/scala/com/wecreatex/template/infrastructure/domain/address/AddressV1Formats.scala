package com.wecreatex.template.infrastructure.domain.address

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.wecreatex.template.domain.address.Address
import com.wecreatex.template.domain.person.Person
import com.wecreatex.utils.json.spray.DefaultJsonFormats
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/** 
 * JSON formats required for the serialization and deserialization of the [[Address]] domain entity. These formats are versioned 
 * according to the API release-versions.
 */
trait AddressV1Formats  extends SprayJsonSupport with DefaultJsonProtocol with DefaultJsonFormats {
  implicit val fmt: RootJsonFormat[Address] = jsonFormat5(Address.apply)
}
