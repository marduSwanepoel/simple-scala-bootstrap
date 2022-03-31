package com.wecreatex.template.domain.person

import com.wecreatex.utils.transport.ResultA

trait PeopleRouter {

  protected type RouteType

  def postPerson: RouteType

  def getPerson: RouteType

  def getRandomPerson: RouteType

}
