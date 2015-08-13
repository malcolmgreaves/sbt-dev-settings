package com.nitro.build

import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{ Docker => TypesafeDocker }

/**
 * Settings that enable a project to be packaged as a Docker container.
 */
object Docker {

  /**
   * Sets the docker base image to the `jvmContainerName`, the exposed port
   * to the relevant input, the docker repository to the given `registry`
   * parameter, and sets update latest to true.
   */
  def settings(
    registry:         String,
    jvmContainerName: String = "java:openjdk-8",
    exposedPort:      Int    = 8092
  ) =
    Seq(
      dockerBaseImage := jvmContainerName,
      dockerExposedPorts in TypesafeDocker := Seq(exposedPort),
      dockerRepository in TypesafeDocker := Some(registry),
      dockerUpdateLatest in TypesafeDocker := true
    )
}