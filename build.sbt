organization := "com.gonitro.platform"

name := "sbt-dev-settings"

version := "0.0.1"

sbtPlugin := true

description := "sbt plugin for standardizing compilation, packaing, formatting, and publishing configurations"

// sbt plugins must be at Scala 2.10.x
scalaVersion := "2.10.5"

// dependices for this plugin

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.4")

// publish settings

publishMavenStyle := true

publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
    else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/malcolmgreaves/sbt-dev-settings</url>
  <licenses>
    <license>
      <name>Apache 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:malcolmgreaves/sbt-dev-settings</url>
    <connection>scm:git:git@github.com:malcolmgreaves/sbt-dev-settings.git</connection>
  </scm>
  <developers>
    <developer>
      <id>malcolmgreaves</id>
      <name>Malcolm Greaves</name>
      <url>https://github.com/malcolmgreaves</url>
    </developer>
    <developer>
      <id>gregsilin</id>
      <name>Greg Silin</name>
      <url>https://github.com/gregsilin</url>
    </developer>
  </developers>
)
