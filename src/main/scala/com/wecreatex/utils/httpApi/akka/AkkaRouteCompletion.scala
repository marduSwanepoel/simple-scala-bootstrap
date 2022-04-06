package com.wecreatex.utils.httpApi.akka

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.wecreatex.utils.json.spray.TransportJsonFormats
import com.wecreatex.utils.logging.LoggingUtils
import com.wecreatex.utils.transport.Fault.GenericFault
import com.wecreatex.utils.transport.{Fault, Result, ResultA}
import monix.execution.Scheduler
import spray.json.DefaultJsonProtocol
import scala.util.{Failure, Success}

/**
 * Akka-specific HTTP API route-completion utility functions, used for easy route completion of all service-specific transport DTOs.
 */
private[akka] trait AkkaRouteCompletion extends LoggingUtils with SprayJsonSupport with DefaultJsonProtocol with TransportJsonFormats {

  private val loggingContextName = "HTTP Route Completion"

  private def completeValue[A](value: A)(implicit marshaller: ToResponseMarshaller[A]): Route = {
    logInfo("HTTP completed with status 200", loggingContextName)
    logDebug(value.toString, loggingContextName)
    toHttpJson(value, StatusCodes.OK)
  }

  private def completeFault(fault: Fault)(implicit marshaller: ToResponseMarshaller[Fault]): Route = {
    val censoredError = GenericFault(fault.message, None)
    logError(s"Completed with status '${StatusCodes.InternalServerError}' and error `${fault.prettyMessage}", loggingContextName)
    toHttpJson(censoredError, StatusCodes.InternalServerError)
  }

  private def completeThrowable(th: Throwable): Route = {
    val censoredError = GenericFault("Response completed with internal error", None)
    completeFault(censoredError)
  }

  final def completeResultWithResponse[A](result: Result[A])(implicit s: Scheduler, marshaller: ToResponseMarshaller[A]): Route = {
    result match {
      case Right(value) => completeValue(value)
      case Left(fault)  => completeFault(fault)
    }
  }

  final implicit class ResultRouteCompletion[A](result: Result[A]) {
    final def completeWithResponse(implicit s: Scheduler, marshaller: ToResponseMarshaller[A]): Route = {
      completeResultWithResponse(result)
    }
  }

    /**
   * Set of route completers that runs and serialises a Result[A]
   */
  final implicit class ResultARouteCompletion[A](result: ResultA[A]) {
      final def completeWithResponse(implicit s: Scheduler, marshaller: ToResponseMarshaller[A]): Route = {
      onComplete(result.runToFuture(s)) {
        case Success(successResult) => completeResultWithResponse(successResult)
        case Failure(th)            => completeThrowable(th)
      }
    }
  }

  /**
   * Set of route completers that runs and serialises a result [A]
   */
  final implicit class AnyRouteCompletion[A](value: A) {
    final def completeWithResponse(implicit marshaller: ToResponseMarshaller[A]): Route =
      completeValue(value)
  }

  //TODO: implement status code
  private def toHttpJson[A](responseBody: A, statusCode: StatusCode)(implicit marshaller: ToResponseMarshaller[A]): Route = {
    complete(responseBody)
  }

  private def toHttpJson(responseBody: String, statusCode: StatusCode): StandardRoute = {
    complete(statusCode, responseBody)
  }
}
