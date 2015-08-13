package com.nitro.build

import com.typesafe.sbt.{ SbtGit => TypesafeSbtGit }

/**
 * Settings that enable a project to use the Typesafe sbt git plugin.
 */
object SbtGit {

  import TypesafeSbtGit._

  // TODO: make version auto-increase based on number of commits since last version.
  lazy val settings = Seq(git.useGitDescribe := true)

}