name := """PlayReactiveMongoPolymer"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.9"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.4-play24",
  "org.specs2" %% "specs2-core" % "3.6.5" % Test,
  "org.specs2" %% "specs2-junit" % "3.6.5" % Test,
  "org.specs2" %% "specs2-mock" % "3.6.5" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node
