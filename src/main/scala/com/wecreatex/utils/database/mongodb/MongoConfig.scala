package com.wecreatex.utils.database.mongodb

import com.wecreatex.utils.database.mongodb.MongoConfig.{Atlas, ConnectionType, Server}
import com.wecreatex.utils.transport.Result
import com.wecreatex.utils.environment.EnvUtils

case class MongoConfig(
  host: String,
  port: Option[Int],
  user: String,
  password: String,
  databaseName: String,
  connectionType: ConnectionType) {

  def connectionString: String = connectionType match {
    case Server => s"mongodb://$user:$password@$host:${port.getOrElse(0)}"
    case Atlas  => s"mongodb+srv://$user:$password@$host"
  }

}

object MongoConfig extends Enumeration {

  type ConnectionType = Value
  val Server, Atlas = Value

  def loadFromEnvUnsafe: MongoConfig = {
    val connectionTypeString = EnvUtils.loadFromEnvUnsafe("MONGO_SERVER_TYPE", "SERVER")
    val connectionType = connectionTypeString match {
      case "SERVER" => Server
      case "ATLAS"  => Atlas
    }

    val host     = EnvUtils.loadFromEnvUnsafe("MONGO_HOST", "localhost")
    val dbName   = EnvUtils.loadFromEnvUnsafe("MONGO_DBNAME", "test-db")
    val user     = EnvUtils.loadFromEnvUnsafe("MONGO_USER", "mongoadmin")
    val password = EnvUtils.loadFromEnvUnsafe("MONGO_PW", "admin")
    val port     = if(connectionType == Atlas) None else
      Some(EnvUtils.loadFromEnvUnsafe("MONGO_PORT", "27017").toInt)

    MongoConfig(host, port, user, password, dbName, connectionType)
  }

  def loadFromEnv: Result[MongoConfig] = Result.attemptUnsafe(loadFromEnvUnsafe)

}