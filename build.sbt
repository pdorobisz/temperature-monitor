name := """temperature-monitor"""
organization := "pdorobisz"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice

libraryDependencies += "com.paulgoldbaum" %% "scala-influxdb-client" % "0.5.2"
