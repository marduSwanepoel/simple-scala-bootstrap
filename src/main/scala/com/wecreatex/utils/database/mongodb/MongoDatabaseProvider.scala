package com.wecreatex.utils.database.mongodb

import com.mongodb.ReadPreference
import com.mongodb.connection.ClusterDescription
import com.wecreatex.utils.transport.{ResultA, Fault, Result}
import io.netty.channel.nio.NioEventLoopGroup
import monix.eval.Task
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{ConnectionString, Document, MongoClient, MongoClientSettings, MongoDatabase, WriteConcern}
import org.mongodb.scala.connection.NettyStreamFactoryFactory
import org.slf4j.Logger
import java.util.concurrent.TimeUnit
import com.wecreatex.utils.transport.TransportImplicits._
import org.mongodb.scala.SingleObservableFuture
import org.mongodb.scala.gridfs.SingleObservableFuture
import org.mongodb.scala.ObservableFuture
import org.mongodb.scala.gridfs.ObservableFuture
import com.wecreatex.utils.database.mongodb.MongoImplicits._

trait MongoDatabaseProvider extends Listeners {

  protected lazy val config: MongoConfig = MongoConfig.loadFromEnvUnsafe
//  def codecs: CodecRegistry

  //TODO: should these be in a thread?
  lazy val mongoClient: MongoClient = createMongoClient
  implicit lazy val mongoDatabase: MongoDatabase = getDatabase
  protected lazy val collections: List[MongoCollectionProvider[_]]

  private var configuration: ClusterDescription = _
//  private def allCodecs = fromRegistries(fromProviders(codecs), DEFAULT_CODEC_REGISTRY)
  private def allCodecs = fromRegistries(DEFAULT_CODEC_REGISTRY)

//todo: wrap settings in try
  lazy private val settings = MongoClientSettings
    .builder()
    .applyToClusterSettings(block => block.serverSelectionTimeout(5, TimeUnit.SECONDS))
    .readPreference(ReadPreference.primary())
    .writeConcern(WriteConcern.MAJORITY)
    .retryWrites(true)
    .addCommandListener(this)
    .codecRegistry(allCodecs)
//    .streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(new NioEventLoopGroup()).build())
    .applyConnectionString(ConnectionString(config.connectionString))
    .build()

  private def createMongoClient: MongoClient =
    MongoClient(settings)

  private def getDatabase: MongoDatabase =
    mongoClient.getDatabase(config.databaseName)

  final def setupCollections: ResultA[Unit] = {
    collections
      .map(_.configureCollection)
      .runParSequence
      .mapToUnit
  }

  //todo this is not failing when no DB / other error
  final def startMongoDb: ResultA[Unit] = {
    logInfo("Starting up..", "Start MongoDB Client")
    pingDatabase(config.databaseName).flatMap(_ => setupCollections)
      .mapToUnit
      .tapRight( _ => logInfo("Successfully connected to ...", "Start MongoDB Client") )
      .tapLeft( fault =>
        logError(
          s"Failed to start MongoDB connection to ${config.databaseName} due to error '${fault.rawMessage}'",
          "Start MongoDB Client")
      )
  }

  final def healthCheck: ResultA[Unit] = {
    pingDatabase()
      .tapRight(_ => logInfo("Successful", "Mongo DB health-check"))
      .tapLeft( fault => logWarn(s"Failed due to '${fault.prettyMessage}'", "Mongo DB health-check"))
      .mapToUnit
  }

  private def pingDatabase(database: String = "config"): ResultA[Document] = {
    logDebug(s"Performing PING to database $database")
    mongoClient
      .getDatabase(database)
      .runCommand(Document("ping" -> 1))
      .runToResultA
  }

}