import Libraries._
import Settings.commonSettings

ThisBuild / organization := "com.creative-x"
ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "3.1.0"

enablePlugins(JavaAppPackaging)

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-bootstrap-api",
    libraryDependencies ++= templateImports)
  .settings(commonSettings)
