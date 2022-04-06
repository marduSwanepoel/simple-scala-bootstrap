package com.wecreatex.utils.httpApi

private[httpApi] trait HttpApiConfig {
  val host: String
  val port: Int
  val parallelism: Int
}