package com.wecreatex.utils.transport

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes.{InternalServerError, OK}
import io.netty.handler.codec.http.HttpResponseStatus
import org.slf4j.Logger
import scala.util.Failure

/***
 * Data Transport Object for domain failures. 
 * Enables the passing around of failures as raw [[Throwable]]s alongside metadata and 
 * formatted messaging
 */
trait Fault  {
  def message: String
  def causedBy: Option[Throwable]
  def prettyMessage: String = Fault.prettyMessage(this)
  def rawMessage: String = Fault.rawMessage(this)
}

object Fault {

  final case class GenericFault(
    message: String,
    causedBy: Option[Throwable]) extends Fault

  def apply(message: String): Fault = 
    GenericFault(message, None)
  
  def apply(cause: Throwable): Fault = 
    GenericFault(cause.getMessage, Some(cause))

  def apply(message: String, cause: Throwable): Fault =
    GenericFault(message, Some(cause))
  
  def apply(message: String, cause: Option[Throwable]): Fault = 
    GenericFault(message, cause)

  //todo move to implicits
  def enrichMessage(prefix: String)(erratum: Fault): Fault =
    Fault(s"$prefix: ${erratum.message}", erratum.causedBy)

  def prettyMessage(e: Fault, prefix: String = "error message: "): String = {
    val message = e.message
    val cause   = e.causedBy.fold("")(m => s" - Exception: $m")
    s"""$prefix$message$cause"""
  }

  def rawMessage(e: Fault): String = {
    prettyMessage(e, "")
  }

  //todo move to implicits
  def log(log: Logger, fault: Fault): Unit = {
    fault.causedBy match {
      case Some(cause) => log.error(fault.message, cause)
      case _ => log.error(fault.message)
    }
  }

}