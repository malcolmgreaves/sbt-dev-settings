package com.nitro.build

import sbt._
import sbt.Keys._

/**
 * Settings for publishing a repository to a Maven-like jar hosting service.
 * Examples include a self-hosted Artifactory, Nexus, or Sonatype (i.e.
 * Maven central).
 */
object Publish {

  import PublishHelpers._

  /**
   * Create the appropriate XML pom for publishing a repository, complete with
   * other publishing related settings.
   */
  def settings(
    repo:       Repository,
    developers: Seq[Dev],
    art:        ArtifactInfo,
    lic:        License
  ) =
    Seq(
      publishMavenStyle := true,
      isSnapshot := art.ver.isSnapshot,
      publishArtifact in Test := false,
      publishTo := {
        if (isSnapshot.value)
          Some("snapshots" at s"${art.location}content/repositories/snapshots")
        else
          Some("releases" at s"${art.location}content/repositories/releases")
      },
      pomIncludeRepository := { _ => false },
      pomExtra := {
        <url>https://${ repo.sourceControl }/${ repo.group }/${ repo.name }</url>
        <licenses>
          <license>
            <name>${ lic.name }</name>
            <url>${ lic.location }</url>
            <distribution>${ lic.distribution }</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@${ repo.sourceControl }:${ repo.group }/${ repo.name }.git</url>
          <connection>scm:git@${ repo.sourceControl }:${ repo.group }/${ repo.name }.git</connection>
        </scm>
        <developers>
          ${ developers.map(developerXml(repo)) }
        </developers>
      }
    )

  /**
   * Create the XML for properly attributing a developer.
   */
  def developerXml(repo: Repository)(dev: Dev) =
    <developer>
      <id>${ dev.id }</id>
      <name>${ dev.name }</name>
      <url>https://${ repo.sourceControl }/${ dev.id }</url>
    </developer>

}