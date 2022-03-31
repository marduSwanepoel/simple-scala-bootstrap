package com.wecreatex.utils.transport

import akka.NotUsed
import cats.data.{EitherT, NonEmptyList}
import cats.data.Validated.{Invalid, Valid}
import com.wecreatex.utils.transport.ResultA.fromResult
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}

/**
 * ResultET -> Result-EitherT
 *
 * A type alias for an [[EitherT]]-converter, wrapping a [[ResultA]], which in return comes down to `EitherT[Task, Fault, A]`
 */
object ResultET extends TransportUtils[ResultET] {

  override def unit: ResultET[Unit] = ResultET.right(())
  override def right[A](a: A): ResultET[A] = EitherT.rightT[Task, Fault](a)
  override def left[Unit](fault: Fault): ResultET[Unit] = EitherT.leftT[Task, Unit](fault)
  
  override def fromResult[A](result: Result[A]): ResultET[A]  = EitherT.fromEither[Task](result)
  private def fromResultA[A](result: ResultA[A]): ResultET[A] = EitherT(result)

}