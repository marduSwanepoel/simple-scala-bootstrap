package com.wecreatex.utils.database.mongodb

import com.wecreatex.utils.transport.{Result, ResultA}
import org.mongodb.scala.{Observable, SingleObservable}

object MongoImplicits {

  implicit class SingleObservableImplicits[T](val observable: SingleObservable[T]) extends AnyVal {
    def runToResultA: ResultA[T] = MongoUtils.runObservableToResultA(observable)
  }

  implicit class ObservableImplicits[T](val observable: Observable[T]) extends AnyVal {
    def runToResultA: ResultA[Seq[T]] = MongoUtils.runObservableToResultA(observable)
  }

  implicit class ResultSingleObservableImplicits[T](val resultObservable: Result[SingleObservable[T]]) extends AnyVal {
    def runToResultA: ResultA[T] = MongoUtils.runRSingleObservableToResultA(resultObservable)
  }

  implicit class ResultObservableImplicits[T](val resultObservable: Result[Observable[T]]) extends AnyVal {
    def runToResultA: ResultA[Seq[T]] = MongoUtils.runRObservableToResultA(resultObservable)
  }

  implicit class ResultASingleObservableImplicits[T](val resultObservable: ResultA[SingleObservable[T]]) extends AnyVal {
    def runToResultA: ResultA[T] = MongoUtils.runRAObservableToResultA(resultObservable)
  }

  implicit class ResultAObservableImplicits[T](val resultObservable: ResultA[Observable[T]]) extends AnyVal {
    def runToResultA: ResultA[Seq[T]] = MongoUtils.runRASingleObservableToResultA(resultObservable)
  }


}