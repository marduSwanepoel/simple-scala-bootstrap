package com.wecreatex.utils.json.spray

import com.wecreatex.utils.transport.Fault
import com.wecreatex.utils.transport.Fault.GenericFault
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat, deserializationError}

/**
 * Collection of application transport DTO JSON formats
 */
trait TransportJsonFormats extends DefaultJsonProtocol {

  implicit object FaultFormat extends RootJsonFormat[Fault] {

    override def write(obj: Fault): JsString = obj match {
      case GenericFault(message, causedBy) => JsString(s"Generic Fault with message: ${obj.prettyMessage}")
      case _ => JsString(s"Unknown Fault with message: ${obj.prettyMessage}")
    }

    override def read(json: JsValue): Fault = deserializationError("Cannot read JSON payload of type Fault")
  }

}