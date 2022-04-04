package com.wecreatex.utils.database.mongodb

import com.wecreatex.utils.transport.{Fault, Result, ResultA}
import monix.eval.Task
import org.mongodb.scala.{Observable, ObservableFuture, SingleObservable, SingleObservableFuture}
import org.mongodb.scala.gridfs.SingleObservableFuture
import org.mongodb.scala.gridfs.ObservableFuture
import com.wecreatex.utils.transport.TransportImplicits.*

object MongoUtils {
  
  def runObservableToResultA[T](observable: Observable[T]): ResultA[Seq[T]] = {
    Task
      .fromFuture(observable.toFuture)
      .attempt
      .map(_.wrapR(th => Fault(th)))
  }

  def runObservableToResultA[T](observable: SingleObservable[T]): ResultA[T] = {
    Task
      .fromFuture(observable.toFuture)
      .attempt
      .map(_.wrapR(th => Fault(th)))
  }

}
