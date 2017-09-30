name := """temperature-monitor"""
organization := "pdorobisz"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, JavaAppPackaging, SystemVPlugin)

scalaVersion := "2.12.3"

libraryDependencies += guice

libraryDependencies += "com.paulgoldbaum" %% "scala-influxdb-client" % "0.5.2"

maintainer in Linux := "Piotr Dorobisz"

packageSummary in Linux := "Temperature and humidity monitor"

packageDescription := "Temperature and humidity monitor"