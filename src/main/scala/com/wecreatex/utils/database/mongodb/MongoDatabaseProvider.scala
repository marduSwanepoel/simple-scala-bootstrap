package com.wecreatex.utils.database.mongodb

import com.mongodb.ReadPreference
import com.wecreatex.utils.database.mongodb.MongoImplicits._
import com.wecreatex.utils.transport.TransportImplicits._
import com.wecreatex.utils.transport.{Fault, Result, ResultA}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{ConnectionString, Document, MongoClient, MongoClientSettings, MongoDatabase, WriteConcern}
import java.util.concurrent.TimeUnit

trait MongoDatabaseProvider extends Listeners {

  val domainRegistries: CodecRegistry
  protected val collections: List[MongoCollectionProvider[_]]

  protected lazy val config: MongoConfig = MongoConfig.loadFromEnvUnsafe

  private def buildSettings() = {
    MongoClientSettings
      .builder()
      .applyToClusterSettings(block => block.serverSelectionTimeout(5, TimeUnit.SECONDS))
      .readPreference(ReadPreference.primary())
      .writeConcern(WriteConcern.MAJORITY)
      .retryWrites(true)
      .addCommandListener(this)
      .codecRegistry(domainRegistries)
      .applyConnectionString(ConnectionString(config.connectionString))
      .build()
  }

  lazy private val settings: Result[MongoClientSettings] = Result.attemptUnsafeWithFault(
    buildSettings(),
    Fault("Failure building MongoDB settings", _)
  )

  lazy val mongoClient: Result[MongoClient] = settings.map(MongoClient(_))

  private final def getDatabase(name: String): Result[MongoDatabase] = mongoClient.map(_.getDatabase(name))
  implicit lazy val mongoDatabase: Result[MongoDatabase] = getDatabase(config.databaseName)

  final private def setupCollections: ResultA[Unit] = {
    collections
      .map(_.ensureCollectionExists)
      .runParSequence
      .mapToUnit
  }

  //todo this is not failing when no DB / other error
  final def startMongoDb: ResultA[Unit] = {
    logInfo("Starting up..", "Start MongoDB Client")
    val tasks = for {
      _ <- pingDatabase(config.databaseName).liftET
      _ <- setupCollections.liftET
    } yield ()

    tasks
      .value
      .tapRight( _ => logInfo(s"Successfully connected to ${config.host}${config.port.fold("")(i => s":$i")} with connection type '${config.connectionType}'", "Start MongoDB Client") )
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
    getDatabase(database)
      .map(_.runCommand(Document("ping" -> 1)))
      .runToResultA
  }

}