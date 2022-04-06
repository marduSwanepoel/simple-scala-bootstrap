package com.wecreatex.utils.application

import com.wecreatex.utils.logging.LoggingUtils
import com.wecreatex.utils.transport.ResultA
import com.wecreatex.utils.transport.TransportImplicits._

/**
 * Provides application startup support for the main application instance. This is the place where all other traits containing the
 * various application instances (repos, services, routers, ..) will be mixed in, dependencies injected, and their startup procedures
 * brought together.
 *
 * The [[instancesStartupImplementation]] method below should be implemented in the final application instance class.
 */
trait ApplicationInstance extends LoggingUtils {

  /** This is the MAIN application start, called from within the application's Boot class. */
  final def start: ResultA[Unit] = {
    logInfo("Commencing with the startup of all application instances", "Application Instances Startup")
    instancesStartupImplementation
      .liftET
      .bimap(
        { fault =>
          logError(s"Unable to start application instances due to `${fault.rawMessage}`", "Application instances startup")
          fault.causedBy.foreach(_.printStackTrace())
          fault
        },
        { _ => logInfo("All application instances started successfully", "Application Instances Startup") }
      ).value
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