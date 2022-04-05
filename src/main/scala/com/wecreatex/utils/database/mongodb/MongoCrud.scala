package com.wecreatex.utils.database.mongodb

import java.time.OffsetDateTime
import akka.event.slf4j.SLF4JLogging
import com.wecreatex.utils.logging.LoggingUtils
import com.wecreatex.utils.transport.{Fault, Result, ResultA}
import com.wecreatex.utils.transport.TransportImplicits._
import monix.eval.Task
import org.bson.conversions.Bson
import org.json4s.Formats
import org.json4s.native.Serialization
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.BsonString
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters.{and, equal, in}
import org.mongodb.scala.model.Projections.{excludeId, fields, include}
import org.mongodb.scala.model.Updates.set
import org.slf4j.Logger
import org.mongodb.scala.SingleObservableFuture
import org.mongodb.scala.gridfs.SingleObservableFuture
import org.mongodb.scala.ObservableFuture
import org.mongodb.scala.gridfs.ObservableFuture
import com.wecreatex.utils.database.mongodb.MongoImplicits._

import scala.concurrent.Future
import scala.reflect.ClassTag
import org.mongodb.scala.result.UpdateResult

private[mongodb] trait MongoCrud[A] extends LoggingUtils {

  implicit val collection: ResultA[MongoCollection[A]]

  def insert(doc: A): Task[Either[Fault,A]] = {
    collection
      .innerMap(_.insertOne(doc).collect())
      .runToResultA
      .innerMap(_ => doc)
  }

//  def insertMany(docs: Seq[A]): Task[Either[ErrorLoad,Seq[A]]] = {
//    val dbFuture = collection
//      .insertMany(docs)
//      .collect()
//      .toFuture()
//    runDbFuture(dbFuture).map(_.map(_ => docs))
//  }
//
//  /**
//   * SET (UPDATE
//   */
//  /**
//   * Updates the document with given id's targeted field with provided value[String]
//   */
//  def setFieldById(id: String, setField: String, newValue: AnyVal)(implicit ct: ClassTag[A], f: Formats, m: Manifest[A]): Task[Either[ErrorLoad,Boolean]] = {
//    val filter = and(equal("id", id), equal("deleted", false))
//    val update = set(setField, newValue)
//    val dbFuture = collection
//      .updateOne(filter, update)
//      .toFuture()
//    runDbFuture(dbFuture).map(_.map(res => res.wasAcknowledged()))
//  }
//
//  def setFieldByTargetField(targetField: String, targetVal: AnyVal, setField: String, setValue: AnyVal)(implicit ct: ClassTag[A], f: Formats, m: Manifest[A]): Result[UpdateResult] = {
//    val filter = and(equal(targetField, targetVal), equal("deleted", false))
//    val update = set(setField, setValue)
//    val dbFuture = collection
//      .updateMany(filter, update)
//      .toFuture()
//    runDbFuture(dbFuture)
//  }
//
//  /**
//   * Updates multiple documents meeting the a targeted field & predicates
//   */
//  def setFieldByTargetFieldValues(targetField: String, targetValues: List[AnyVal], setField: String, setValue: AnyVal)(implicit ct: ClassTag[A], f: Formats, m: Manifest[A]): Task[Either[ErrorLoad,Boolean]] = {
//    val filter = and(in(targetField, targetValues.toSeq: _*), equal("deleted", false))
//    val update = set(setField, setValue)
//    val dbFuture = collection
//      .updateMany(filter, update)
//      .toFuture()
//    runDbFuture(dbFuture).map(_.map(res => res.wasAcknowledged()))
//  }
//
//  /**
//   * Updates multiple documents based on a custome filter provided
//   */
//  def setByFilter(filter: Bson, setField: String, setVal: Boolean)(implicit ct: ClassTag[A], f: Formats, m: Manifest[A]): Task[Either[ErrorLoad,Boolean]] = {
//    val update = set(setField, setVal)
//    val dbFuture = collection
//      .updateMany(filter, update)
//      .toFuture()
//    runDbFuture(dbFuture).map(_.map(res => res.wasAcknowledged()))
//  }
//
//  /**
//   * FIND (READ)
//   */
//  def findByQuery[B](filter: Bson, returnFields: List[String] = List.empty[String])(implicit ct: ClassTag[B]): Result[Seq[B]] = {
//    val dbFuture = collection
//      .find[B](filter)
//      .projection(fields(include(returnFields: _*), excludeId()))
//      .collect()
//      .toFuture
//    runDbFuture(dbFuture)
//  }
//  def findByField(field: String, value: AnyVal)(implicit ct: ClassTag[A]): Result[Seq[A]] = {
//    val filter = and(equal(field, value), equal("deleted", false))
//    val dbFuture = collection
//      .find[A](filter)
//      .limit(1)
//      .collect()
//      .toFuture
//    runDbFuture(dbFuture)
//  }
//  /**
//   * Finds documents by filtering on a field.
//   * Sorts them from latest to oldest
//   */
//  def findByFieldSorted(field: String, value: AnyVal)(implicit ct: ClassTag[A]): Result[Seq[A]] = {
//    val filter = and(equal(field, value), equal("deleted", false))
//    val dbFuture = collection
//      .find[A](filter)
//      .sort(equal("_id", -1))
//      .collect()
//      .toFuture
//    runDbFuture(dbFuture)
//  }
//
//  def findByFieldValues[B](field: String, values: List[AnyVal], returnFields: List[String] = List.empty[String])(implicit ct: ClassTag[B]): Result[Seq[B]] = {
//    val dbFuture = collection
//      .find[B](and(in(field, values.toSeq: _*), equal("deleted", false)))
//      .projection(fields(include(returnFields: _*), excludeId()))
//      .toFuture()
//    runDbFuture(dbFuture)
//  }
//
//  def findAll()(implicit ct: ClassTag[A]): Result[Seq[A]] = {
//    val filter = equal("deleted", false)
//    val dbFuture = collection
//      .find[A](filter)
//      .toFuture()
//    runDbFuture(dbFuture)
//  }
//
//  def findById(id: String)(implicit ct: ClassTag[A]): Task[Either[ErrorLoad,Option[A]]] = {
//    val filter = and(equal("id", id), equal("deleted", false))
//    val dbFuture = collection
//      .find[A](filter)
//      .limit(1)
//      .collect()
//      .toFuture
//    runDbFuture(dbFuture).map(_.map(_.headOption))
//  }
//
//  def findByIds(ids: List[String])(implicit ct: ClassTag[A]): Result[Seq[A]] = {
//    val dbFuture = collection
//      .find(and(in("id", ids.toSeq: _*), equal("deleted", false)))
//      .toFuture()
//    runDbFuture(dbFuture)
//  }
//
//  def findByIds[B](ids: List[String], returnFields: List[String] = List.empty[String])(implicit ct: ClassTag[B]): Result[Seq[B]] = {
//    val dbFuture = collection
//      .find[B](and(in("id", ids.toSeq: _*), equal("deleted", false)))
//      .projection(fields(include(returnFields: _*), excludeId()))
//      .toFuture()
//    runDbFuture(dbFuture)
//  }
//
//  /**
//   * COUNT
//   */
//  def countByField(field: String, value: AnyVal)(implicit ct: ClassTag[A]): Result[Long] = {
//    val filter = and(equal(field, value), equal("deleted", false))
//    val dbFuture = collection
//      .countDocuments(filter)
//      .toFuture
//    runDbFuture(dbFuture)
//  }
//  def countByFilter(filterIn: Bson)(implicit ct: ClassTag[A]): Result[Long] = {
//    val filter = and(filterIn, equal("deleted", false))
//    val dbFuture = collection
//      .countDocuments(filter)
//      .toFuture
//    runDbFuture(dbFuture)
//  }
//
//  /**
//   * EXISTS (READ)
//   */
//  def existsByFilter(filter: Bson)(implicit ct: ClassTag[A]): Task[Either[ErrorLoad,Boolean]] = {
//    val dbFuture = collection
//      .countDocuments(filter)
//      .toFuture
//    runDbFuture(dbFuture).map(_.map(_>0))
//  }
//  /**
//   * Gets the distinct values of the specified field name.
//   */
//  def distinctValuesPerField(fieldName: String)(implicit ct: ClassTag[String]): Result[Seq[String]] = {
//    val dbFuture = collection
//      .distinct[String](fieldName)
//      .collect()
//      .toFuture()
//    runDbFuture(dbFuture)
//  }
//
//
//  /**
//   * DELETE
//   */
//  def deleteById(id: String)(implicit ct: ClassTag[A], f: Formats, m: Manifest[A]): Task[Either[ErrorLoad,Boolean]] = {
//    val filter = and(
//      equal("id", id),
//      equal("deleted", false)
//    )
//    val update = set("deleted", true)
//    val dbFuture = collection
//      .updateOne(filter, update)
//      .toFuture()
//    runDbFuture(dbFuture).map(_.map(res => res.wasAcknowledged()))
//  }
//
//  def updateById(id: String, newDoc: A)(implicit ct: ClassTag[A], f: Formats, m: Manifest[A]): Result[A] = {
//    val filter = and(equal("id", id), equal("deleted", false))
//    val replacement = replacementWithId(newDoc, id)
//    val dbFuture = collection
//      .findOneAndReplace(filter, replacement)
//      .toFuture()
//    runDbFuture(dbFuture)
//  }
//
//  def aggregateByPipeline[B](pipeline: Seq[Bson])(implicit ct: ClassTag[B]): Result[Seq[B]] = {
//    val dbFuture = collection
//      .aggregate[B](pipeline)
//      .toFuture()
//    runDbFuture(dbFuture)
//  }
//
  private def runDbFuture[A](dbFuture: Future[A]): ResultA[A] = {
    Task
      .deferFuture(dbFuture)
      .attempt
      .map(_.left.map(Fault("Error executing Mongo operation", _)))
      .doOnFinish(logOutcome)
  }
//
//  private def replacementWithId(doc: A, id: String)(implicit f: Formats, m: Manifest[A]): A = {
//    val newDoc = toDocument(doc).-("updatedAt", "id") ++ Document(
//      "updatedAt" -> BsonString(OffsetDateTime.now().toString),
//      "id" -> BsonString(id)
//    )
//    fromDocument(newDoc)
//  }
//
//  private def toDocument[A <: AnyRef : Manifest](a: A)(implicit f: Formats): Document = {
//    Document(Serialization.write(a))
//  }
//
//  private def fromDocument[A <: AnyRef : Manifest](a: Document)(implicit f: Formats): A = {
//    Serialization.read[A](a.toJson())
//  }
//
  private def logOutcome(opt: Option[Throwable]): Task[Unit] = {
    Task.apply {
      opt match {
        case None     => logInfo("Successfully executed operation.", "Mongo CRUD Operation")
        case Some(ex) => logError(s"Error executing operation with message '${ex.getMessage}'", "Mongo CRUD Operation")
      }
    }
  }
}
