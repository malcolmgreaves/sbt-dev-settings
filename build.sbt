organization := "com.gonitro.platform"

name := "sbt-nitro-dev-settings"

version := "0.0.1"

sbtPlugin := true

description := "sbt plugin for standard build configurations."

// sbt plugins must be at Scala 2.10.x
scalaVersion := "2.10.5"

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
  <url>https://github.com/malcolmgreaves/sbt-nitro-dev-settings</url>
  <licenses>
    <license>
      <name>Apache 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:malcolmgreaves/sbt-nitro-dev-settings</url>
    <connection>scm:git:git@github.com:malcolmgreaves/sbt-nitro-dev-settings.git</connection>
  </scm>
  <developers>
    <developer>
      <id>malcolmgreaves</id>
      <name>Malcolm Greaves</name>
      <url>https://github.com/malcolmgreaves</url>
    </developer>
  </developers>
)
