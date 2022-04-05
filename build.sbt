import Libraries._
import Settings.commonSettings

ThisBuild / organization := "com.we-create-x"
ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "2.13.8"

enablePlugins(JavaAppPackaging)

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-simple-bootstrap",
    libraryDependencies ++= templateImports)
  .settings(commonSettings)
