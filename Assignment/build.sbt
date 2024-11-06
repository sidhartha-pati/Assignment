ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.2"

lazy val root = (project in file("."))
  .settings(
    name := "Assignment"
  )

libraryDependencies ++= Seq(
  "org.apache.flink" %% "flink-streaming-scala" % "1.16.0",
  "org.apache.flink" % "flink-connector-kafka" % "3.3.0-1.20",
  "org.apache.flink" %% "flink-scala" % "1.16.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.0",
  "org.apache.flink" % "flink-connector-jdbc" % "3.2.0-1.19"
)

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.3.0-SNAP2" % Test
libraryDependencies += "org.postgresql" % "postgresql" % "42.5.3"