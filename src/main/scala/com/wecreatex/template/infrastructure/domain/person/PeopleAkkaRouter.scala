package com.wecreatex.template.infrastructure.domain.person

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.scaladsl.{Flow, Sink}
import com.wecreatex.template.domain.person.{PeopleService, Person, PeopleRouter}
import com.wecreatex.utils.httpApi.akka.AkkaApiRouter
import com.wecreatex.utils.time.TimeUtils.nowDate
import monix.execution.Scheduler
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

/** Infrastructure-specific implementation of the [[PeopleRouter]], implemented with AKKA HTTP */
class PeopleAkkaRouter(service: PeopleService)(implicit val scheduler: Scheduler) extends PeopleRouter with AkkaApiRouter with PersonV1Formats {

  override type RouteType = Route
  override val apiVersion: String = "v1"
  override val baseUrlOpt: Option[String] = Some("people")

  override lazy val routes: Route = getRandomPerson ~ getPerson ~ postPerson

  def getPerson: RouteType = apiGet(Segment) { personId =>
    service
      .getPersonById(personId)
      .completeWithResponse
  }

  def postPerson: Route = (apiPost()
    & withEntity[Person]) { person =>
    service
      .createPerson(person)
      .completeWithResponse
  }

  def getRandomPerson: Route = apiGet("random") {
    service
      .generateRandomPerson
      .completeWithResponse
  }

}