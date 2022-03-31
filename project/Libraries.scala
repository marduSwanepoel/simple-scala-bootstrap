import sbt._

object Libraries {

  private object Versions {
    val akka          = "2.6.10"
    val akkaScala3    = "2.6.19"
    val akkaHttp      = "10.2.9"
    val monix         = "3.4.0"
    val mongoScalaDriver = "4.1.1"
    val cats          = "2.7.0"
    val netty         = "4.1.17.Final"
    val json4s        = "4.0.4"
    val akkaHttpSpray = "10.1.12"
    val bsonCodec     = "1.0.1"
    val slf4jApi      = "1.7.25"
    val slf4jLog      = "1.7.25"
    val bcrypt        = "4.1"
    val javaMail      = "5.1.6"
    val pureCsv       = "0.3.3"
    val scalaCache    = "0.28.0"
  }

  val akkaActor  = "com.typesafe.akka"   %% "akka-actor"                 % Versions.akkaScala3 cross CrossVersion.for3Use2_13
  val akkaStream = "com.typesafe.akka"   %% "akka-stream"                % Versions.akkaScala3 cross CrossVersion.for3Use2_13
  val akkaHttp   = "com.typesafe.akka"   %% "akka-http"                  % Versions.akkaHttp cross CrossVersion.for3Use2_13
  val akkaHttpSpray = "com.typesafe.akka"%% "akka-http-spray-json"       % Versions.akkaHttp cross CrossVersion.for3Use2_13
  val akkaSLF4J  = "com.typesafe.akka"   %% "akka-slf4j"                 % Versions.akkaScala3 // cross CrossVersion.for3Use2_13
  val monix      = "io.monix"            %% "monix"                      % Versions.monix
  val mongoScalaDriver = "org.mongodb.scala" %% "mongo-scala-driver"     % Versions.mongoScalaDriver cross CrossVersion.for3Use2_13
  val catsCore   = "org.typelevel"       %% "cats-core"                  % Versions.cats
  val catsKernel = "org.typelevel"       %% "cats-kernel"                % Versions.cats
  val netty     = "io.netty"             %  "netty-all"                  % Versions.netty
  val json4s    = "org.json4s"           %% "json4s-native"              % Versions.json4s
  val bsonCodec = "ch.rasc"              % "bsoncodec"                   % Versions.bsonCodec
  val slf4jApi = "org.slf4j"             % "slf4j-api"                   % Versions.slf4jApi
  val slf4jLog = "org.slf4j"             % "slf4j-log4j12"               % Versions.slf4jLog
  val bcrypt   = "com.github.t3hnar"     %% "scala-bcrypt"               % Versions.bcrypt cross CrossVersion.for3Use2_13
  val javaMail = "org.simplejavamail"    % "simple-java-mail"            % Versions.javaMail
  val pureCsv  = "io.kontainers"         %% "purecsv"                    % Versions.pureCsv cross CrossVersion.for3Use2_13
  val scalaCache = "com.github.cb372"    %% "scalacache-redis"           % Versions.scalaCache cross CrossVersion.for3Use2_13
  val scalaCacheCats = "com.github.cb372" %% "scalacache-cats-effect"    % Versions.scalaCache cross CrossVersion.for3Use2_13

  val templateImports = Seq(
    Libraries.akkaActor,
    Libraries.akkaStream,
    Libraries.akkaHttp,
    Libraries.akkaSLF4J,
    Libraries.monix,
    Libraries.mongoScalaDriver,
    Libraries.catsCore,
    Libraries.catsKernel,
    Libraries.netty,
    Libraries.json4s,
    Libraries.akkaHttpSpray,
    Libraries.bsonCodec,
    Libraries.slf4jApi,
    Libraries.slf4jLog,
    Libraries.bcrypt,
    Libraries.javaMail,
    Libraries.pureCsv,
    Libraries.scalaCache,
    Libraries.scalaCacheCats
  )

}
