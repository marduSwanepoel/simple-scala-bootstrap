package com.wecreatex.utils.httpApi.akka

import akka.actor.ActorSystem
import com.wecreatex.utils.httpApi.HttpApiConfig
import com.wecreatex.utils.transport.Result
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.Scheduler

case class AkkaServerConfig(
  host: String,
  port: Int,
  parallelism: Int,
  system: ActorSystem,
  scheduler: Scheduler) extends HttpApiConfig

object AkkaServerConfig { 
  
  private def getSystem = ActorSystem(s"http-api-actorSystem")
  
  private def getScheduler = Scheduler.io(
    name = s"http-api-scheduler",
    executionModel = AlwaysAsyncExecution)

  final def loadDefault: AkkaServerConfig = {
    AkkaServerConfig("0.0.0.0", 21001, 8, getSystem, getScheduler)
  }

  //TODO implement
  final def loadFromEnv: Result[AkkaServerConfig] = {
    Result.right(AkkaServerConfig("0.0.0.0", 21001, 8, getSystem, getScheduler))
  }
  
}