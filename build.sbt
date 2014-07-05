name := "scala-retry"

version := "0.1"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
  "org.slf4j" % "slf4j-api" % "1.6.4",
  "org.slf4j" % "slf4j-simple" % "1.6.4"
)