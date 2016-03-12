name := "scala-retry"

organization := "com.github.hipjim"

crossScalaVersions := Seq("2.10.6", "2.11.7")

version := "0.3.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-Xlint",
  "-target:jvm-1.6",
  "-Yinline-warnings",
  "-optimise",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Xlog-free-terms"
)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
)