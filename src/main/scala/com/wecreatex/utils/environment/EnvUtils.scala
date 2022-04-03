package com.wecreatex.utils.environment

import com.wecreatex.utils.transport.{Result, Fault}

object EnvUtils {

  /**
   * Loads an environmental variable from environment.
   * If ENV_LOCAL_DEV=true the localDefault value is used if no value
   * is found in the environment
   *
   * If ENV_LOCAL_DEV=false a startup exception will be thrown if no value
   * is found in the environment
   */
  def loadFromEnvUnsafe(key: String, devDefault: String): String = {
    val env   = System.getenv()
    val isDev = env.getOrDefault("ENV_LOCAL_DEV", "false").toBoolean

    if (isDev) env.getOrDefault(key, devDefault)
    else env.get(key)
  }

  def loadFromEnv(key: String, devDefault: String): Result[String] = {
    Result
      .attemptUnsafeWithFault(
        loadFromEnvUnsafe(key, devDefault),
        _ => Fault(s"Environment variable $key not found")
      )
  }

}