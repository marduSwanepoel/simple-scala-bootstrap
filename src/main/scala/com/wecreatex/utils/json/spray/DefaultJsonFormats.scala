package com.wecreatex.utils.json.spray

import spray.json.{DefaultJsonProtocol, DeserializationException, JsNull, JsString, JsValue, RootJsonFormat, deserializationError}

import java.time.{LocalDate, OffsetDateTime, Period}
import scala.util.Try

/**
 * Collection of default spray-json based JSON formats
 */
trait DefaultJsonFormats extends DefaultJsonProtocol {

  //  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {
  //    override def write(obj: DateTime) = JsString(obj.toString)
  //
  //    override def read(json: JsValue): DateTime = json match {
  //      case JsString(s) => parseUTCDateTime(s)
  //      case _ => throw new DeserializationException("Invalid date, expected: yyyy-MM-dd'T'HH:mm:ssZZ")
  //    }
  //  }
  //
  //  def parseUTCDateTime(s: String): DateTime = {
  //    if ("now".equalsIgnoreCase(s)) nowUTC
  //    else OffsetDateTime
  //      .parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME) //we can accept any zoned date
  //      .atZoneSameInstant(UTCZone)
  //      .toOffsetDateTime
  //  }

  implicit object UnitFormat extends RootJsonFormat[Unit] {

    override def write(obj: Unit): JsValue = JsNull

    override def read(json: JsValue): Unit = json match {
      case JsNull => ()
      case _ => deserializationError("Invalid JSON received, expected: Unit")
    }
  }

  implicit object PeriodFormat extends RootJsonFormat[Period] {

    override def write(obj: Period): JsString = JsString(obj.toString)

    override def read(json: JsValue): Period = json match {
      case JsString(s) => Period.parse(s)
      case _ => deserializationError("Invalid period, expected: PyYmMwWdDThHmMsS")
    }
  }

  implicit object DateFormat extends RootJsonFormat[LocalDate] {

    def write(obj: LocalDate): JsValue = {
      JsString(obj.toString)
    }

    def read(json: JsValue): LocalDate = json match {
      case JsString(s) => Try(LocalDate.parse(s)).getOrElse(error(s))
      case _ => error(json.toString())
    }

    def error(v: Any): LocalDate = {
      deserializationError(
        s"""
           |'$v' is not a valid date value.""".stripMargin
      )
    }
  }

  implicit object DateTimeFormat extends RootJsonFormat[OffsetDateTime] {

    def write(obj: OffsetDateTime): JsValue = {
      JsString(obj.toString)
    }

    def read(json: JsValue): OffsetDateTime = json match {
      case JsString(s) => Try(OffsetDateTime.parse(s)).getOrElse(error(s))
      case _ => error(json.toString())
    }

    def error(v: Any): OffsetDateTime = {
      deserializationError(
        s"""
           |'$v' is not a valid date value. Dates must be in format:
           |     * date-opt-time     = date-element ['T' [time-element] [offset]]
           |     * date-element      = std-date-element | ord-date-element | week-date-element
           |     * std-date-element  = yyyy ['-' MM ['-' dd]]
           |     * ord-date-element  = yyyy ['-' DDD]
           |     * week-date-element = xxxx '-W' ww ['-' e]
           |     * time-element      = HH [minute-element] | [fraction]
           |     * minute-element    = ':' mm [second-element] | [fraction]
           |     * second-element    = ':' ss [fraction]
           |     * offset            = 'Z' | (('+' | '-') HH [':' mm [':' ss [('.' | ',') SSS]]])
           |     * fraction          = ('.' | ',') digit+
        """.stripMargin
      )
    }
  }

}