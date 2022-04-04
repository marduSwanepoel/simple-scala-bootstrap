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

  implicit class EitherErrorImplicits[A](val either: Either[Throwable, A]) extends AnyVal {
    def wrapR(ifError: Throwable => Fault): Result[A] = either match {
      case Left(th)     => Result.left(ifError(th))
      case Right(value) => Result.right(value)
    }
  }

  implicit class FaultImplicits(val fault: Fault) extends AnyVal {
    def liftR: Result[Unit] = resultUtils.left(fault)
    def liftA: ResultA[Unit] = resultAUtils.left(fault)
    def liftET: ResultET[Unit] = resultETUtils.left(fault)
  }

  //TODO test this and if it works, convert the rest to use this format
//  trait TransportUtilsImplicits[M[_], A](val target: M[A])(implicit util: TransportUtils[M]) {
//
//    def ensureElseFault(predicate: => Boolean, ifFalse: => Fault): M[A] =
//      util.conditional(predicate, ifFalse)(target)
//
//  }

  implicit class ResultImplicits[A](val result: Result[A]) extends AnyVal {
    def liftA: ResultA[A] = resultAUtils.fromResult(result)
    def liftET: ResultET[A] = resultETUtils.fromResult(result)

    def tapRight(fr: A => Unit): Result[A]    = resultUtils.tapRight(result, fr)
    def tapLeft(fl: Fault => Unit): Result[A] = resultUtils.tapLeft(result, fl)

    def ensureElseFault(predicate: => Boolean, ifFalse: => Fault): Result[A] =
      resultUtils.conditional(predicate, ifFalse)(result)

    def ensureElseReplace(predicate: => Boolean, ifFalse: Result[A]): Result[A] =
      resultUtils.conditional(predicate, result, ifFalse)

    def mapToUnit: Result[Unit] = resultUtils.mapToUnit(result)
  }

  implicit class ResultAImplicits[A](val result: ResultA[A]) extends AnyVal {

    def liftET: ResultET[A] = ResultET.fromResultA(result)
    
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

    def tapRight(fr: A => Unit): ResultA[A]    = resultAUtils.tapRight(result, fr)
    def tapLeft(fl: Fault => Unit): ResultA[A] = resultAUtils.tapLeft(result, fl)

    def mapToUnit: ResultA[Unit] = resultAUtils.mapToUnit(result)
  }


  implicit class ResultAsImplicits[A](val results: Seq[ResultA[A]]) extends AnyVal {
    def runSequence: ResultA[Seq[A]] = ResultA.runSequence(results)

    def runParSequence: ResultA[Seq[A]] = ResultA.runParSequence(results)

    def runParSequenceUnordered: ResultA[Seq[A]] = ResultA.runParSequenceUnordered(results)
  }

  implicit class ResultETImplicits[A](val result: ResultET[A]) extends AnyVal {
    def tapRight(fr: A => Unit): ResultET[A]    = resultETUtils.tapRight(result, fr)
    def tapLeft(fl: Fault => Unit): ResultET[A] = resultETUtils.tapLeft(result, fl)

    def ensureElseFault(predicate: => Boolean, ifFalse: => Fault): ResultET[A] =
      resultETUtils.conditional(predicate, ifFalse)(result)

    def ensureElseReplace(predicate: => Boolean, ifFalse: ResultET[A]): ResultET[A] =
      resultETUtils.conditional(predicate, result, ifFalse)

    def mapToUnit: ResultET[Unit] = resultETUtils.mapToUnit(result)
  }

}