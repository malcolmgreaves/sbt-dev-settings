package com.nitro.build

import scala.util.Try

/**
 * Data structures for publish settings. Encapsulates information about
 * the developers, the hosted repository, the artifact publish location,
 * the project version, and the project's license.
 */
object PublishHelpers {

  type Url = String

  case class SemanticVersion(major: Short, minor: Short, patch: Short, isSnapshot: Boolean) {
    override def toString =
      s"""$major.$minor.$patch${if(isSnapshot) "-SNAPSHOT" else ""}"""
  }

  object SemanticVersion {
    
    def parse(ver: String): Option[SemanticVersion] =
      Try {
        val bits = ver.split("\\.")
        val (major, minor, patchSnap) = (bits(0), bits(1), bits(2))
        val (patch, isSnapshot) = {
          val sbits = patchSnap.split("-")
          (sbits(0), sbits.size == 2)
        }
        SemanticVersion(
          major = major.toShort,
          minor = minor.toShort,
          patch = patch.toShort,
          isSnapshot = isSnapshot
        )
      }
      .toOption
  }

  /**
   * Contains the name and group for a git repoistory.
   */
  case class Repository(
    name:          String,
    group:         String,
    sourceControl: Url
  )

  object Repository {

    /**
     * Creates a Repository instance with `sourceControl` populated with Github.
     */
    def github(name: String, group: String): Repository =
      Repository(
        name = name,
        group = group,
        sourceControl = "github.com"
      )

    /**
     * Creates a Repository instance with `sourceControl` populated with Bitbucket.
     */
    def bitbucket(name: String, group: String): Repository =
      Repository(
        name = name,
        group = group,
        sourceControl = "bitbucket.org"
      )

  }

  /**
   * Contains the location of the Maven-like jar repository as well as whether
   * or not to publish as a release (otherwise it's a snapshot).
   */
  case class ArtifactInfo(
    snapshot:  Url,
    release: Url,
    ver: SemanticVersion
  )

  object ArtifactInfo {

    /**
     * An Artifact instance that is useful for pushing releases to Sonatype.
     */
    def sonatype(v: SemanticVersion) =
      ArtifactInfo(
        snapshot = "https://oss.sonatype.org/content/repositories/snapshots",
        release= "https://oss.sonatype.org/service/local/staging/deploy/maven2",
        ver = v
      )
  }

  /**
   * Contains licensing information for the code.
   */
  case class License(name: String, location: Url, distribution: String)

  object License {

    /**
     * A License instance for Apache 2.0
     */
    val apache20 =
      License(
        name = "Apache 2.0",
        location = "http://www.apache.org/licenses/LICENSE-2.0.txt",
        distribution = "repo"
      )

    /**
     * A License instance for BSD 3-Clause
     */
    val bsd =
      License(
        name = "BSD 3-Clause",
        location = "http://choosealicense.com/licenses/bsd-3-clause/",
        distribution = "repo"
      )

    /**
     * A License instance for GPL version 3.0
     */
    val gpl3 =
      License(
        name = "GPL v3.0",
        location = "http://choosealicense.com/licenses/gpl-3.0/",
        distribution = "repo"
      )

    /**
     * A License instance for the Lesser GPL version 2.1
     */
    val lgpl21 =
      License(
        name = "L-GPL v2.1",
        location = "http://choosealicense.com/licenses/lgpl-2.1/",
        distribution = "repo"
      )

    /**
     * A License instance for MIT
     */
    val mit =
      License(
        name = "MIT",
        location = "http://choosealicense.com/licenses/mit/",
        distribution = "repo"
      )

    /**
     * A License instance that permits absolutely no sharing. All rights are
     * reserved by the Copyright holder.
     */
    def noLicense(owner: String) =
      License(
        name = s"No License. All Copyrights Reserved by $owner",
        location = "http://choosealicense.com/licenses/no-license/",
        distribution = "repo"
      )
  }

}