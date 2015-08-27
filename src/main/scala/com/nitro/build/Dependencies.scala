package com.nitro.build

import sbt.Keys._
import sbt._

object Dependencies {

  /**
   * Logging dependencies
   */
  object Logging {

    // TODO switch to journal from oncue once they support 2.11
    lazy val settings = Seq(
      libraryDependencies ++= Seq("com.typesafe.scala-logging" %% "scala-logging" % "3.1.0")
    )
  }
}