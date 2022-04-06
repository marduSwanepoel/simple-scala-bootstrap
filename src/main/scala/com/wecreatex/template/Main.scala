package com.wecreatex.template

import com.wecreatex.template.application.ScalaBootstrapApplicationInstance
import com.wecreatex.utils.application.BootSupport
import com.wecreatex.utils.transport.ResultA

/*** Application entrypoint */
object Main extends BootSupport {

  lazy val serverInstance = new ScalaBootstrapApplicationInstance()

  override protected def startup: ResultA[Unit] = {
    serverInstance.start
  }

}