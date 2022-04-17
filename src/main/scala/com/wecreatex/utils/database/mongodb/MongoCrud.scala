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
import org.mongodb.scala.model.Filters.{and, equal, exists, in, or, empty}
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

  private val idFieldName = "id"
  private val deletedFieldName = "deleted"

  /**
   * Creates a filter that matches all documents where the value of a field equals any value in the list of specified values.
   */
  private def filterOnField[F >: AnyVal](fieldName: String, values: Seq[F]): Bson = {
    in(fieldName, values: _*)
  }

  private def filterWithValuesMap[F >: AnyVal](fieldsValuesMap: Map[String, F]): Bson = {
    val fieldsMatches = fieldsValuesMap.map{ case (field, value) => equal(field, value) }.toSeq
    and(fieldsMatches: _*)
  }

  private implicit class BsonFilerOps(filter: Bson) {
    def excludeDeleted: Bson = {
      and(
        filter,
        or(exists(deletedFieldName, exists = false), equal(deletedFieldName, value = false))
      )
    }
  }

  /**CREATE*/

  def insert(doc: A): ResultA[A] = {
    collection
      .innerMap(_.insertOne(doc).collect())
      .runToResultA
      .innerMap(_ => doc)
  }

  def insertMany(docs: Seq[A]): ResultA[Seq[A]] = {
    collection
      .innerMap(_.insertMany(docs).collect())
      .runToResultA
      .innerMap(_ => docs)
  }

  /** READ */

  /** Gets exactly one entry that matches on its ID field. If more that one or no result are found, a [[Fault]] is returned. */
  def getById(id: String)(implicit ct: ClassTag[A]): ResultA[A] = {
    findOneById(id)
      .map(_.flatMap(_.liftR(s"Entity with ID '$id' not found")))
  }

  /** Finds one or None entry that matches on its ID field. If more than one entry is found, a [[Fault]] is returned */
  def findOneById(id: String)(implicit ct: ClassTag[A]): ResultA[Option[A]] =
    findOneByField(idFieldName, id)

  /** Gets exactly one entry that matches on any field-value combination. If more that one or no result are found, a [[Fault]] is returned. */
  def getByField[F >: AnyVal](field: String, value: F)(implicit ct: ClassTag[A]): ResultA[A] = {
    findOneByField(field, value)
      .map(_.flatMap(_.liftR(s"Entity with lookup on field '$field' not found")))
  }

  /** Finds one or None entry that matches on ID. If more than one entry is found, a [[Fault]] is returned */
  def findOneByField[F >: AnyVal](field: String, value: F)(implicit ct: ClassTag[A]): ResultA[Option[A]] = {
    findManyByField(field, Seq(value), 2)
      .map(ensureOneOrNone(_, s"More than one entity entity matching on field '$field' found"))
  }

  /** Finds many entries that matches on a id -> Seq[id] combination. Every entry for which the id field matches any of the ids passed in, is returned */
  def findManyById(ids: Seq[String], limit: Int = 200)(implicit ct: ClassTag[A]): ResultA[Seq[A]] =
    findManyByField(idFieldName, ids, limit)

  /** Finds many entries that matches on a field -> Seq[value] combination. Every entry for which the selected field matches any of the values is returned */
  def findManyByField[F >: AnyVal](fieldName: String, values: Seq[F], limit: Int = 200)(implicit ct: ClassTag[A]): ResultA[Seq[A]] = {
    val filter = filterOnField(fieldName, values).excludeDeleted
    findManyByQuery(filter, limit)
  }

  def getByMap[F >: AnyVal](fieldsValuesMap: Map[String, F])(implicit ct: ClassTag[A]): ResultA[A] = {
    findOneByFields(fieldsValuesMap)
      .map(_.flatMap(_.liftR(s"Entity with lookup on fields '${fieldsValuesMap.keySet.mkString(",")}' not found")))
  }

  def findOneByFields[F >: AnyVal](fieldsValuesMap: Map[String, F])(implicit ct: ClassTag[A]): ResultA[Option[A]] = {
    findManyByFields(fieldsValuesMap, limit = 2)
      .map(ensureOneOrNone(_, s"More than one entity entity matching on fields '${fieldsValuesMap.keySet.mkString(",")}' found"))
  }

  def findManyByFields[F >: AnyVal](fieldsValuesMap: Map[String, F], limit: Int = 200)(implicit ct: ClassTag[A]): ResultA[Seq[A]] = {
    val filter = filterWithValuesMap(fieldsValuesMap).excludeDeleted
    findManyByQuery(filter, limit)
  }

  def findOneByQuery(filter: Bson)(implicit ct: ClassTag[A]): ResultA[Option[A]] = {
    findManyByQuery(filter, limit = 2)
      .map(ensureOneOrNone(_, "More than one entity matching in findOneByQuery"))
  }

  def findAll(limit: Int)(implicit ct: ClassTag[A]): ResultA[Seq[A]] = {
    def filter = empty().excludeDeleted
    findManyByQuery(filter, limit)
  }

  def findManyByQuery(filter: Bson, limit: Int = 200)(implicit ct: ClassTag[A]): ResultA[Seq[A]] = {
    collection
      .innerMap { _.find[A](filter)
        .limit(limit)
        .collect()
      }
      .runToResultA
  }

  private def ensureOneOrNone(result: Result[Seq[A]], errorMessage: String): Result[Option[A]] = {
    result match {
      case Right(result::Nil) => Result.right(Some(result))
      case Right(Nil)         => Result.right(None)
      case Left(err)          => Result.left(err)
      case _                  => Result.left(errorMessage)
    }
  }

  /** UPDATE */


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


//  def updateById(id: String) = {
//    collection.innerMap(_.updateOne())
//  }



  /** DELETE */



  //TODO implement DELETE verify check
  def HARDDeleteById(id: String): ResultA[Unit] = {
    collection
      .innerMap {
        _.deleteOne(equal("id", id))
          .collect()
      }.runToResultA
      .mapToUnit
  }


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

}
