package com.wecreatex.utils.database.mongodb

import org.mongodb.scala.{MongoCollection, MongoDatabase}
import com.wecreatex.utils.transport.TransportImplicits._
import scala.reflect.ClassTag
import com.wecreatex.utils.database.mongodb.MongoImplicits._
import com.wecreatex.utils.transport.ResultA

trait MongoCollectionProvider[A : ClassTag](implicit database: MongoDatabase) extends MongoCrud[A] {

  protected lazy val collectionName: String
  override val collection: MongoCollection[A] = database.getCollection[A](collectionName)

  def configureCollection: ResultA[Unit] = {
    collection
      .listIndexes()
      .runToResultA
      .mapToUnit
  }

}