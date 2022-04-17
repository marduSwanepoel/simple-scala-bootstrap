package com.wecreatex.utils.database.mongodb

import com.wecreatex.utils.transport.{Result, ResultA}
import com.wecreatex.utils.transport.TransportImplicits._
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import scala.reflect.ClassTag

abstract class MongoCollectionProvider[A : ClassTag] extends MongoCrud[A] {

  val database: Result[MongoDatabase]
  protected val collectionName: String

  override lazy val collection: ResultA[MongoCollection[A]] = database.map(_.getCollection[A](collectionName)).liftA

  def ensureCollectionExists: ResultA[Unit] = collection.mapToUnit

}