package com.wecreatex.utils.database.mongodb

import com.wecreatex.utils.transport.ResultA
import org.mongodb.scala.{Observable, SingleObservable}

object MongoImplicits {
  
  implicit class ObservableImplicits[T](val observable: Observable[T]) extends AnyVal {
    def runToResultA: ResultA[Seq[T]] = MongoUtils.runObservableToResultA(observable)
  }

  implicit class SingleObservableImplicits[T](val observable: SingleObservable[T]) extends AnyVal {
    def runToResultA: ResultA[T] = MongoUtils.runObservableToResultA(observable)
  }

}