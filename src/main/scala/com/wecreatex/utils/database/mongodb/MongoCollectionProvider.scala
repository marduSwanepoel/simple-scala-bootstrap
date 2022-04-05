package com.wecreatex.utils.database.mongodb

import com.wecreatex.utils.database.mongodb.MongoImplicits._
import com.wecreatex.utils.transport.ResultA
import com.wecreatex.utils.transport.TransportImplicits._
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import scala.reflect.ClassTag

abstract class MongoCollectionProvider[A : ClassTag] extends MongoCrud[A] {

  val database: MongoDatabase
  protected val collectionName: String

  override lazy val collection: MongoCollection[A] = database.getCollection[A](collectionName)

  def configureCollection: ResultA[Unit] = {
    collection
      .listIndexes()
      .runToResultA
      .mapToUnit
  }

}