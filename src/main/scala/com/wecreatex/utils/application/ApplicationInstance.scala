package com.wecreatex.utils.application

import cats.effect.ExitCode
import com.wecreatex.utils.logging.LoggingUtils
import com.wecreatex.utils.transport.ResultA
import monix.eval.Task

/**
 * Provides application startup support for the main application instance. This is the place where all other traits containing the
 * various application instances (repos, services, routers, ..) will be mixed in, dependencies injected, and their startup procedures
 * brought together.
 *
 * The [[instancesStartupImplementation]] method below should be implemented in the final application instance class.
 */
trait ApplicationInstance extends LoggingUtils {

  /** This is the MAIN application start, called from within the application's Boot class. */
  final def start: Task[ExitCode] = {
    logInfo("Commencing with the startup of all application instances", "Application Instances Startup")
    instancesStartupImplementation.map {
      case Left(fault) =>
        logError(s"Unable to start application instances due to `${fault.rawMessage}`", "Application instances startup")
        fault.causedBy.foreach(_.printStackTrace())
        ExitCode.Error
      case Right(value) =>
        logInfo("All instances started successfully", "Application Instances Startup")
        ExitCode.Success
    }
  }

  /**
   * This method forms part of the template-method design pattern implementation followed in this trait. This method hooks into
   * the [[start]] method, and should be implemented in the final application instance class.
   *
   * It should implement all the startup procedures for the various parts mixed into the final [[ApplicationInstance]], ex:
   * {{{
   *   protected def instancesStartupImplementation: ResultA[Unit] = {
   *      for {
   *        _ <- httpApi.startAndBind()
   *        _ <- mongoDbServer.start()
   *        _ <- kafkaClient.start()
   *        _ <- ...
   *      } yield someOtherResource.start()
   *   }
   * }}} */
  protected def instancesStartupImplementation: ResultA[Unit]

}