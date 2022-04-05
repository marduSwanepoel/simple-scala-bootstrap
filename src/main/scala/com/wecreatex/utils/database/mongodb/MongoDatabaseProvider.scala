package com.wecreatex.utils.database.mongodb

import com.mongodb.ReadPreference
import com.wecreatex.template.infrastructure.domain.person.PeopleMongoRepository
import com.wecreatex.utils.database.mongodb.MongoImplicits._
import com.wecreatex.utils.transport.ResultA
import com.wecreatex.utils.transport.TransportImplicits._
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{ConnectionString, Document, MongoClient, MongoClientSettings, MongoDatabase, WriteConcern}

import java.util.concurrent.TimeUnit

trait MongoDatabaseProvider extends Listeners {

  //  private var configuration: ClusterDescription = _
  protected lazy val config: MongoConfig = MongoConfig.loadFromEnvUnsafe


  val domainRegistries: CodecRegistry
//  import scala.jdk.CollectionConverters._
//  val codecRegistries: CodecRegistry = fromRegistries(
//    collectionRegistries.asJava
//    //    collectionRegistries:_*
//    //    DEFAULT_CODEC_REGISTRY
//  )

  //todo: wrap settings in try
  lazy private val settings = MongoClientSettings
    .builder()
    .applyToClusterSettings(block => block.serverSelectionTimeout(5, TimeUnit.SECONDS))
    .readPreference(ReadPreference.primary())
    .writeConcern(WriteConcern.MAJORITY)
    .retryWrites(true)
    .addCommandListener(this)
    .codecRegistry(domainRegistries)
    //    .streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(new NioEventLoopGroup()).build())
    .applyConnectionString(ConnectionString(config.connectionString))
    .build()

  protected val collections: List[MongoCollectionProvider[_]]

  //TODO: should these be in a thread?
  lazy val mongoClient: MongoClient = MongoClient(settings)
  implicit lazy val mongoDatabase: MongoDatabase = mongoClient.getDatabase(config.databaseName)

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