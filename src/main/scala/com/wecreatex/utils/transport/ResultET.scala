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
  def fromResultA[A](result: ResultA[A]): ResultET[A] = EitherT(result)
  
  override def tapRight[A](self: ResultET[A], fr: A => Unit): ResultET[A] = {
    self.bimap(
      { fault => fault },
      { value => try fr(value); value } //try and ignore failure
    )
  }

  override def tapLeft[A](self: ResultET[A], fl: Fault => Unit): ResultET[A] = {
    self.bimap(
      { fault => try fl(fault); fault },
      { value => value }
    )
  }

  override def mapToUnit[A](transport: ResultET[A]): ResultET[Unit] =
    transport.map(_ => ())

}