package com.wecreatex.template.domain.address

trait AddressRouter {

  protected type RouteType

  def getAddress: RouteType

  def postAddress: RouteType

}
