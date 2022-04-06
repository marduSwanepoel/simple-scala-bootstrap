package com.wecreatex.utils.application

import com.wecreatex.utils.logging.LoggingUtils
import com.wecreatex.utils.transport.ResultA
import monix.execution.Scheduler
import scala.concurrent.duration._
import scala.concurrent.Await

/**
 * Contains needed support for service startup functionality.
 *
 * The `startup()` method should be implemented on extension, containing all the startup checks and procedures
 * that the service needs to run through on startup.
 */
trait BootSupport extends App with LoggingUtils {

  /** Single thread scheduler used to only run the `startup()` process */
  private val startupScheduler = Scheduler.singleThread("application-boot-thread", daemonic = true)

  /** Cleans up resources, produces logs and shuts down the JVM. */
  private final def shutDownAndExit(ex: Option[Throwable]) = {
    logError("Error while booting application,  cleaning up and exiting..", "Application Boot")
    ex.foreach(_.printStackTrace())
    System.exit(1)
  }

  /** Coupling with the application instances' (and other) startup procedures that should be run on startup */
  protected def startup: ResultA[Unit]

  /** The main startup flow that is executed when the extended class is run. */
  try {
    Await.result(startup.runToFuture(startupScheduler), 20.seconds) match {
      case Right(_) => logInfo("Application started successfully", "Application Boot")
      case Left(ex) => shutDownAndExit(ex.causedBy)
    }
  } catch {
    case ex: Throwable => shutDownAndExit(Some(ex))
  }

}