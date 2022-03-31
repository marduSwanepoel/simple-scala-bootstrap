package com.wecreatex.utils.transport

import cats.Applicative
import cats.data.EitherT
import monix.eval.Task
import com.wecreatex.utils.transport.TransportUtils
import scala.util.control.NonFatal
import scala.util.Either

/**
 * A Scala [[Either]]-based DTO used to transfer either a generic type of anything as a [[Right]], or 
 * a [[Fault]] as a [[Left]].
 * 
 * This enables the strict implementation of the functional programming paradigm throughout the codebase.
 */
object Result extends TransportUtils[Result] {

  //pure functions
  override def unit: Result[Unit] = Result.right(())
  override def right[A](a: A): Result[A] = Right(a)
  override def left[Unit](fault: Fault): Result[Unit] = Left(fault)

  override def fromResult[A](result: Result[A]): Result[A] = result

}