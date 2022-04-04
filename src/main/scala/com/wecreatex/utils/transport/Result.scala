package com.wecreatex.utils.transport

import cats.{Applicative, Traverse, UnorderedTraverse}
import cats.data.EitherT
import monix.eval.Task
import com.wecreatex.utils.transport.TransportUtils

import scala.util.control.NonFatal
import scala.util.Either
import cats.implicits.*
import cats.NonEmptyTraverse.ops.toAllNonEmptyTraverseOps

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

  override def tapRight[A](self: Result[A], fr: A => Unit): Result[A] = self match {
    case Right(value) => Result
      .attemptUnsafe(fr(value))
      .flatMap(_ => self)
    case _ => self
  }

  override def tapLeft[A](self: Result[A], fl: Fault => Unit): Result[A] = self match {
    case Left(fault) => Result
      .attemptUnsafe(fl(fault))
      .flatMap(_ => self)
    case _ => self
  }

  override def mapToUnit[A](transport: Result[A]): Result[Unit] =
    transport.map(_ => ())

  //todo test
  def invert[A](results: Seq[Result[A]]): Result[Seq[A]] = {
    results.foldLeft(Result.right(Seq.empty[A])) { case (accumulator, result) =>
      (accumulator, result) match {
        case (Right(seq), Right(value)) => Result.right(seq :+ value)
        case (Left(err), _) => accumulator
      }
    }
  }

}