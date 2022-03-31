package com.wecreatex.utils.transport

import scala.util.control.NonFatal

object TransportImplicits {

  lazy implicit val resultUtils: TransportUtils[Result] = Result
  lazy implicit val resultAUtils: TransportUtils[ResultA] = ResultA
  lazy implicit val resultETUtils: TransportUtils[ResultET] = ResultET

  implicit class OptionsImplicits[A](val option: Option[A]) extends AnyVal {
    def liftR(ifNone: => Fault): Result[A] = resultUtils.fromOption(option, ifNone)
    def liftA(ifNone: => Fault): ResultA[A] = resultAUtils.fromOption(option, ifNone)
    def liftET(ifNone: => Fault): ResultET[A] = resultETUtils.fromOption(option, ifNone)

    def liftR(message: String): Result[A] = resultUtils.fromOption(option, Fault.apply(message))
    def liftA(message: String): ResultA[A] = resultAUtils.fromOption(option, Fault.apply(message))
    def liftET(message: String): ResultET[A] = resultETUtils.fromOption(option, Fault.apply(message))
  }

  implicit class FaultImplicits(val fault: Fault) extends AnyVal {
    def liftR: Result[Unit] = resultUtils.left(fault)
    def liftA: ResultA[Unit] = resultAUtils.left(fault)
    def liftET: ResultET[Unit] = resultETUtils.left(fault)
  }

  trait TransportUtilsImplicits[M[_], A](val target: M[A])(implicit util: TransportUtils[M]) {

    def ensureElseFault(predicate: => Boolean, ifFalse: => Fault): M[A] =
      util.conditional(predicate, ifFalse)(target)

  }

  //TODO test this and if it works, convert the rest to use this format
  implicit class ResultImplicitsTT[A](val result: Result[A]) extends TransportUtilsImplicits[Result, A](result) {


  }

  implicit class ResultImplicits[A](val result: Result[A]) extends AnyVal {
    def liftA: ResultA[A] = resultAUtils.fromResult(result)
    def liftET: ResultET[A] = resultETUtils.fromResult(result)

    def ensureElseFault(predicate: => Boolean, ifFalse: => Fault): Result[A] =
      resultUtils.conditional(predicate, ifFalse)(result)

    def ensureElseReplace(predicate: => Boolean, ifFalse: Result[A]): Result[A] =
      resultUtils.conditional(predicate, result, ifFalse)
  }

  implicit class ResultAImplicits[A](val result: ResultA[A]) extends AnyVal {
    def ensureElseFault(predicate: => Boolean, ifFalse: => Fault): ResultA[A] =
      resultAUtils.conditional(predicate, ifFalse)(result)

    def ensureElseReplace(predicate: => Boolean, ifFalse: ResultA[A]): ResultA[A] =
      resultAUtils.conditional(predicate, result, ifFalse)

    def tap(code: Result[A] => Unit): ResultA[A] = {
      result.map { value =>
        try code(value)
        catch {
          case NonFatal(_) => //Ignore side effect
        }
        value
      }
    }

    def tapRight(code: A => Unit): ResultA[A]    = result.tap(_.foreach(code))
    def tapLeft(code: Fault => Unit): ResultA[A] = result.tap(_.left.foreach(code))
  }

  implicit class ResultETImplicits[A](val result: ResultET[A]) extends AnyVal {
    def ensureElseFault(predicate: => Boolean, ifFalse: => Fault): ResultET[A] =
      resultETUtils.conditional(predicate, ifFalse)(result)

    def ensureElseReplace(predicate: => Boolean, ifFalse: ResultET[A]): ResultET[A] =
      resultETUtils.conditional(predicate, result, ifFalse)
  }

}