package com.wecreatex.utils.httpApi

import com.wecreatex.utils.transport.Result
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.Scheduler

trait HttpApiConfig {
  val host: String
  val port: Int
  val parallelism: Int
}