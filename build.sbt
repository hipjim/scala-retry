name := "scala-retry"

organization := "com.github.hipjim"

crossScalaVersions := Seq("2.10.6", "2.11.7")

version := "0.1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-target:jvm-1.6",
  // turns all warnings into errors ;-)
  //"-Xfatal-warnings",
  // possibly old/deprecated linter options
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Yinline-warnings",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Xlog-free-terms",
  // enables linter options
  "-Xlint:adapted-args", // warn if an argument list is modified to match the receiver
  "-Xlint:nullary-unit", // warn when nullary methods return Unit
  "-Xlint:inaccessible", // warn about inaccessible types in method signatures
  "-Xlint:nullary-override", // warn when non-nullary `def f()' overrides nullary `def f'
  "-Xlint:infer-any", // warn when a type argument is inferred to be `Any`
  "-Xlint:-missing-interpolator", // disables missing interpolator warning
  "-Xlint:doc-detached", // a ScalaDoc comment appears to be detached from its element
  "-Xlint:private-shadow", // a private field (or class parameter) shadows a superclass field
  "-Xlint:type-parameter-shadow", // a local type parameter shadows a type already in scope
  "-Xlint:poly-implicit-overload", // parameterized overloaded implicit methods are not visible as view bounds
  "-Xlint:option-implicit", // Option.apply used implicit view
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit
  "-Xlint:by-name-right-associative", // By-name parameter of right associative operator
  "-Xlint:package-object-classes", // Class or object defined in package object
  "-Xlint:unsound-match" // Pattern match may not be typesafe
)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
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
  <url>https://github.com/monixio/sincron/</url>
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