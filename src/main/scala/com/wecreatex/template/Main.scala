package com.wecreatex.template

import akka.event.slf4j.SLF4JLogging
import cats.effect.ExitCode
import com.wecreatex.template.domain.address.AddressService
import com.wecreatex.template.domain.person.PeopleService
import com.wecreatex.template.infrastructure.domain.address.AddressAkkaRouter
import monix.eval.{Task, TaskApp}
import akka.http.scaladsl.server.Directives._
import com.wecreatex.template.application.ScalaBootstrapApplicationInstance
import com.wecreatex.utils.httpApi.akka.AkkaServerConfig

/*** Application entrypoint */
object Main extends TaskApp {

  def run(args: List[String]): Task[ExitCode] = {
    val serverInstance = new ScalaBootstrapApplicationInstance()
    serverInstance.start
  }

}