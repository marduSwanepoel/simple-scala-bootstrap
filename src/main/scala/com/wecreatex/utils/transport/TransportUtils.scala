package com.wecreatex.utils.transport

import monix.eval.Task
import scala.util.control.NonFatal

trait TransportUtils[M[_]] {

  def unit: M[Unit]
  def right[A](a: A): M[A]
  def left[A](fault: Fault): M[A]
  def left[A](message: String): M[A] = left(Fault(message))
  def left[Unit](ex: Throwable): M[Unit] = left(Fault(ex))

  def fromResult[A](result: Result[A]): M[A]

  /**
   * Extracts a value out of the option or returns an [[Erratum]]
   * @param opt
   * @param ifNone Function used to create the error if None was found
   * @tparam A
   * @return
   */
  final def fromOption[A](opt: Option[A], ifNone: => Fault): M[A] =
    opt.fold[M[A]](left(ifNone))(a => right(a))

  final def fromException[A](ex: Throwable): M[A] =
    left(Fault(ex))

  /** Lifts an unsafe code block and specifies the [[Fault]] with a function */
  final def attemptUnsafeWithFault[A](unsafe: => A, ifError: Throwable => Fault): M[A] = {
    try right(unsafe)
    catch {
      case NonFatal(cause) => left(ifError(cause))
    }
  }

  final def attemptUnsafe[A](unsafe: => A): M[A] =
    attemptUnsafeWithFault[A](unsafe, (ex: Throwable) => Fault(ex))

  /**
   * Tests a predicate to return a [[M]] or [[Fault]] based on the predicate
   *
   * @param predicate assertion resulting in a [[Boolean]] predicate
   * @param result if predicate is TRUE, returns this [[M]]
   * @param ifFalse  if predicate is FALSE, returns this [[Fault]]
   */
  final def conditional[A](predicate: => Boolean, ifFalse: => Fault)(ifTrue: M[A] = unit): M[A] = {
    if (predicate) ifTrue
    else this.left(ifFalse)
  }

  final def conditional[A](predicate: => Boolean, ifTrue: M[A], ifFalse: M[A]): M[A] =
    if (predicate) ifTrue else ifFalse

}