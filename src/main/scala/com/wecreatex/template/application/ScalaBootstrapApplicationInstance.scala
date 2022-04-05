package com.wecreatex.template.application

import akka.event.slf4j.SLF4JLogging
import cats.effect.ExitCode
import com.wecreatex.template.application.{ServiceInstances, HttpApiInstance}
import com.wecreatex.template.infrastructure.domain.address.AddressAkkaRouter
import com.wecreatex.utils.application.ApplicationInstance
import com.wecreatex.utils.httpApi.akka.{AkkaHttpApi, AkkaApiRouter}
import com.wecreatex.utils.logging.LoggingUtils
import com.wecreatex.utils.transport.ResultA
import com.wecreatex.utils.transport.TransportImplicits._
import monix.eval.Task

/**
 * Brings together all the different components within this application, injecting the various dependencies and 
 * binding everything into a single application instances that can be run from the main Boot class.
 * 
 * It binds the HTTP API Instance containing the routers ([[HttpApiInstance]]), the domain services ([[ServiceInstances]]), and
 * the repository instances ([[InMemoryRepoInstances]]).
 * 
 * */
class ScalaBootstrapApplicationInstance 
  extends ApplicationInstance 
    with HttpApiInstance 
    with ServiceInstances
    with InMemoryRepoInstances
    with MongoCollectionInstances {

  override protected def instancesStartupImplementation: ResultA[Unit] = {
    val result = for {
      _ <- startHttpApiFromEnvironment().liftET
      _ <- startMongoDb.liftET
    } yield ()
    result.value
  }

}