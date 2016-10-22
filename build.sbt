import play.sbt.routes.RoutesKeys

name := "PlayReactiveMongoPolymer"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.1"
) ++ Seq("core", "junit", "mock").map(m =>
  "org.specs2" %% s"specs2-$m" % "3.6.5" % Test)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

RoutesKeys.routesImport += "play.modules.reactivemongo.PathBindables._"
