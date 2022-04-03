package com.wecreatex.utils.database.mongodb

import akka.event.slf4j.SLF4JLogging
import com.mongodb.event.{ClusterListener, ClusterOpeningEvent, CommandFailedEvent, CommandListener, CommandStartedEvent, CommandSucceededEvent, ServerListener, ServerMonitorListener, ServerOpeningEvent}
import com.wecreatex.utils.logging.LoggingUtils
import java.util.concurrent.TimeUnit.MILLISECONDS

trait Listeners extends CommandListener with ClusterListener with ServerListener with ServerMonitorListener with LoggingUtils {

  override def commandStarted(event: CommandStartedEvent): Unit = 
    logDebug(s"Writing command ${event.getCommandName} to database ${event.getDatabaseName}, at server ${event.getConnectionDescription.getServerAddress}", "Mongodb-cmd-started")

  override def commandSucceeded(event: CommandSucceededEvent): Unit = 
    logInfo(s"Completed command ${event.getCommandName} in ${event.getElapsedTime(MILLISECONDS)} ms, to server ${event.getConnectionDescription.getServerAddress}", "Mongodb-cmd-completed")

  override def commandFailed(event: CommandFailedEvent): Unit = 
    logError(s"Failed with message '${event.getThrowable.getMessage}' in ${event.getElapsedTime(MILLISECONDS)} ms, to server ${event.getConnectionDescription.getServerAddress}", "Mongodb-cmd-failed")

  override def clusterOpening(event: ClusterOpeningEvent): Unit = 
    logInfo(s"Opened connection to cluster ${event.getClusterId.getDescription}", "Mongodb-cluster-opening")

  override def serverOpening(event: ServerOpeningEvent): Unit = 
    logInfo(s"Opened connection to server ${event.getServerId.getAddress}", "Mongodb-server-opening")
  
}