package com.wecreatex.utils.logging

import akka.event.slf4j.SLF4JLogging
import org.slf4j

/**
 * Centralised logging framework.
 * 
 * Provides some logging utility functions, but attempts to centralise the logging functionality in order to keep refactoring central
 * and decouple logging from the infrastructure implementation
 */
trait LoggingUtils extends SLF4JLogging {

  private val infoColor  = Console.GREEN
  private val debugColor = Console.CYAN
  private val warnColor  = Console.YELLOW
  private val errorColor = Console.RED

  private def infoLogger: String => Unit  = log.info
  private def debugLogger: String => Unit = log.debug
  private def warnLogger: String => Unit  = log.warn
  private def errorLogger: String => Unit = log.error

  final protected def logInfo(message: String, context: String): Unit =
    logWithFormatting(message, Some(context), infoColor, infoLogger)

  final protected def logInfo(message: String): Unit =
    logWithFormatting(message, None, infoColor, infoLogger)

  final protected def logDebug(message: String, context: String): Unit =
    logWithFormatting(message, Some(context), debugColor, debugLogger)

  final protected def logDebug(message: String): Unit =
    logWithFormatting(message, None, debugColor, debugLogger)

  final protected def logWarn(message: String, context: String): Unit =
    logWithFormatting(message, Some(context), warnColor, warnLogger)

  final protected def logWarn(message: String): Unit =
    logWithFormatting(message, None, warnColor, warnLogger)

  final protected def logError(message: String, context: String): Unit =
    logWithFormatting(message, Some(context), errorColor, errorLogger)

  final protected def logError(message: String): Unit =
    logWithFormatting(message, None, errorColor, errorLogger)

  private def logWithFormatting(
    message: String, 
    contextOpt: Option[String], 
    contextColor: String, 
    log: String => Unit): Unit = {
    contextOpt.fold(
      log(s"${contextColor}Unknown context: ${Console.BLUE}$message${Console.RESET}"))( context => 
      log(s"$contextColor$context: ${Console.BLUE}$message${Console.RESET}"))
  }

}