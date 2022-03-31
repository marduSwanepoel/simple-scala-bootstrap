import sbt.Keys._
import sbt.Level
import sbt._
import scala.collection.immutable.Seq

object Settings {

  val libraryResolvers: Seq[Resolver] = Seq(
    Resolver.typesafeRepo("releases"),
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/",
    Resolver.bintrayRepo("cakesolutions", "maven"),
    "mvnrepository" at "https://mvnrepository.com/artifact/",
    "jitpack" at "https://jitpack.io",
    Resolver.mavenCentral
  )
  
  lazy val commonSettings = Seq(
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:implicitConversions",
      "-language:_",
      "-language:existentials",
      "-encoding",
      "utf8"),
    resolvers ++= libraryResolvers,
    logLevel in compile := Level.Debug,
    updateOptions := updateOptions.value.withCachedResolution(true)
  )
  
}