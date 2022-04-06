package com.wecreatex.template.infrastructure.domain.person

import akka.http.scaladsl.server.Route
import com.wecreatex.template.domain.person.{PeopleRouter, PeopleService, Person}
import com.wecreatex.utils.httpApi.akka.AkkaApiRouter
import monix.execution.Scheduler

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