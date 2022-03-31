package com.wecreatex.utils

import cats.Applicative
import cats.data.EitherT
import monix.eval.Task
import java.time.OffsetDateTime
import scala.util.control.NonFatal

package object transport {
  
  type Result[+A]  = Either[Fault, A]
  type ResultA[A]  = Task[Result[A]]
  type ResultET[A] = EitherT[Task, Fault, A]
  
}