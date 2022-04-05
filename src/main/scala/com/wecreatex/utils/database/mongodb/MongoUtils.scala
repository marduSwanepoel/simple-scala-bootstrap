package com.wecreatex.utils.database.mongodb

import com.wecreatex.utils.transport.{Fault, Result, ResultA}
import monix.eval.Task
import org.mongodb.scala.{Observable, ObservableFuture, SingleObservable, SingleObservableFuture}
import org.mongodb.scala.gridfs.SingleObservableFuture
import org.mongodb.scala.gridfs.ObservableFuture
import com.wecreatex.utils.transport.TransportImplicits._

object MongoUtils {

  def runObservableToResultA[T](observable: SingleObservable[T]): ResultA[T] = {
    Task
      .fromFuture(observable.toFuture)
      .attempt
      .map(_.wrapR(th => Fault(th)))
  }

  def runObservableToResultA[T](observable: Observable[T]): ResultA[Seq[T]] = {
    Task
      .fromFuture(observable.toFuture)
      .attempt
      .map(_.wrapR(th => Fault(th)))
  }

  //todo can we DRY Result to TransportUtil?
  def runRSingleObservableToResultA[T](observableResult: Result[SingleObservable[T]]): ResultA[T] = {
    val result = for {
      observable <- observableResult.liftET
      resultA    <- runObservableToResultA(observable).liftET
    } yield resultA
    result.value
  }

  def runRObservableToResultA[T](observableResult: Result[Observable[T]]): ResultA[Seq[T]] = {
    val result = for {
      observable <- observableResult.liftET
      resultA    <- runObservableToResultA(observable).liftET
    } yield resultA
    result.value
  }

  def runRAObservableToResultA[T](observableResult: ResultA[SingleObservable[T]]): ResultA[T] = {
    val result = for {
      observable <- observableResult.liftET
      resultA    <- runObservableToResultA(observable).liftET
    } yield resultA
    result.value
  }

  def runRASingleObservableToResultA[T](observableResult: ResultA[Observable[T]]): ResultA[Seq[T]] = {
    val result = for {
      observable <- observableResult.liftET
      resultA    <- runObservableToResultA(observable).liftET
    } yield resultA
    result.value
  }

}