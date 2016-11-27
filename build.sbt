name := """demo-play"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  filters,
  "org.webjars" % "bootstrap" % "3.3.7-1",
  "org.webjars" % "requirejs" % "2.3.2",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.12"
)

pipelineStages := Seq(rjs, digest, gzip)