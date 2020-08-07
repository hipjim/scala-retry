name := "scala-retry"

organization := "com.github.hipjim"

crossScalaVersions := Seq("2.10.6", "2.11.10", "2.12.8","2.13.1")

version := "0.5.0-SNAPSHOT"

scalaVersion := "2.13.1"

scalacOptions ++= Seq(
  // warnings
  "-unchecked", // able additional warnings where generated code depends on assumptions
  "-deprecation", // emit warning for usages of deprecated APIs
  "-feature", // emit warning usages of features that should be imported explicitly
  // Features enabled by default
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros"  
)

// Targeting Java 6, but only for Scala <= 2.11
javacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, majorVersion)) if majorVersion <= 11 =>
    // generates code with the Java 6 class format
    Seq("-source", "1.6", "-target", "1.6")
  case _ =>
    // For 2.12 we are targeting the Java 8 class format
    Seq("-source", "1.8", "-target", "1.8")
})

// Targeting Java 6, but only for Scala <= 2.11
scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, majorVersion)) if majorVersion <= 11 =>
    // generates code with the Java 6 class format
    Seq("-target:jvm-1.6")
  case _ =>
    // For 2.12 we are targeting the Java 8 class format
    Seq.empty
})

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.1" % "test"
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false
pomIncludeRepository := { _ => false }

pomExtra :=
  <url>https://github.com/hipjim/scala-retry</url>
    <licenses>
      <license>
        <name>Apache License, Version 2.0</name>
        <url>https://www.apache.org/licenses/LICENSE-2.0</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:hipjim/scala-retry.git</url>
      <connection>scm:git:git@github.com:hipjim/scala-retry.git</connection>
    </scm>
    <developers>
      <developer>
        <id>hipjim</id>
        <name>Cristian Popovici</name>
        <url>https://cristipopovici.com</url>
      </developer>
    </developers>