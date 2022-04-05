package com.wecreatex.utils.transport

import cats.Applicative
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import cats.instances.list._
import cats.instances.either._
import cats.syntax.traverse._
import monix.execution.compat.BuildFrom

/**
 * ResultA -> Result-Async
 *
 * An asynchronous, Monix [[Task]]-based DTO that wraps the [[Result]] DTO type.
 *
 * This is the primary, and nearly only accepted, non-blocking channel through which data is passed around between functions. The
 * codebase follows a strict non-blocking approach, requiring any blocking code to be run as a [[ResultA]] - which takes care of the underlying
 * mechanics using a Monix [[Task]].
 */
object ResultA extends TransportUtils[ResultA] {

  override def unit: ResultA[Unit] = right(())
  override def right[A](a: A): ResultA[A] = Task.pure(Right(a))
  override def left[Unit](fault: Fault): ResultA[Unit] = Task.pure(Left(fault))
  
  override def fromResult[A](result: Result[A]): ResultA[A] =
    Task.pure(result)

  override def tapRight[A](result: ResultA[A], fr: A => Unit): ResultA[A] = {
    result.map(Result.tapRight(_, fr))
  }

  override def tapLeft[A](result: ResultA[A], fl: Fault => Unit): ResultA[A] = {
    result.map(Result.tapLeft(_, fl))
  }

  def innerMap[A, B](result: ResultA[A], f: A => B): ResultA[B] = {
    result.map(_.map(f))
  }

//  def innerFlatMap[A, B](result: ResultA[A], f: A => ResultA[B]): ResultA[B] = {
//    result.flatMap[B](res => res.flatMap(f))
//  }
  
  def fromFuture[A](future: Future[A])(implicit ec: ExecutionContext): ResultA[A] = {
    val futureResult = future
      .map[Result[A]](Right(_))
      .recover {
        case NonFatal(ex) => Left(Fault(ex))
      }
    Task.fromFuture(futureResult)
  }

  override def mapToUnit[A](transport: ResultA[A]): ResultA[Unit] =
    transport.map(_.map(_ => ()))

  def runSequence[A](results: Seq[ResultA[A]]): ResultA[Seq[A]] = {
    Task
      .sequence(results)
      .map(Result.invert)
  }

  def runParSequence[A](resultAs: Seq[ResultA[A]]): ResultA[Seq[A]] = {
    Task
      .parSequence(resultAs)
      .map(Result.invert)
  }

  def runParSequenceUnordered[A](resultAs: Seq[ResultA[A]]): ResultA[Seq[A]] = {
    Task
      .parSequenceUnordered(resultAs)
      .map(Result.invert)
  }

}