package com.wecreatex.utils

import com.wecreatex.utils.transport.Result
import java.time.format.DateTimeFormatter
import java.time.{OffsetDateTime, ZoneOffset, LocalDate}
import scala.util.control.NonFatal

package object time {

  type Date        = LocalDate
  type DateTime    = OffsetDateTime
  type DateTimeOpt = Option[DateTime]

  val UTCZone: ZoneOffset = ZoneOffset.UTC
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

}
