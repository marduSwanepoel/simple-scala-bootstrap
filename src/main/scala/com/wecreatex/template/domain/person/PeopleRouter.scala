package com.wecreatex.template.domain.person

trait PeopleRouter {

  protected type RouteType

  def postPerson: RouteType

  def getPerson: RouteType

  def deletePerson: RouteType

}
