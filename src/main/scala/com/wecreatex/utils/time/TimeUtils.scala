package com.wecreatex.utils.time

import com.wecreatex.utils.transport.Result
import java.time.OffsetDateTime
import java.time.{OffsetDateTime, ZoneOffset, LocalDate}
import java.time.format.DateTimeFormatter
import scala.util.control.NonFatal

object TimeUtils {

  final def nowDate: Date = LocalDate.now(UTCZone)

  final def parseDate(year: Int, month: Int, dayOfMonth: Int): Result[Date] = {
    try {
      val date = LocalDate.of(year, month, dayOfMonth)
      Result.right(date)
    } catch {
      case NonFatal(ex) => Result.left(ex)
    }
  }

  final def nowDateTime: DateTime = OffsetDateTime.now(UTCZone)

  final def parseDateTime(s: String): Result[DateTime] = {
    try {
      val dateTime = OffsetDateTime
        .parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        .atZoneSameInstant(UTCZone)
        .toOffsetDateTime
      Result.right(dateTime)
    } catch {
      case NonFatal(ex) => Result.left(ex)
    }
  }

  final def parseDateTimeToOpt(s: String): DateTimeOpt =
    parseDateTime(s).toOption

}
