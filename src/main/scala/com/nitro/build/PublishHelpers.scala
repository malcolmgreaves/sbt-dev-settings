package com.nitro.build

/**
 * Data structures for publish settings.
 */
object PublishHelpers {

  type Url = String

  /**
   * Represents a developer by their user ID and real name
   * (nickname or anonymous handle is ok too).
   *
   * For example, if the repository is on Github, then `id` is the username.
   */
  case class Developer(id: String, name: String)

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
  }

  /**
   * Contains the location of the Maven-like jar repository as well as whether
   * or not to publish as a release (otherwise it's a snapshot).
   */
  case class Artifact(
    location:  Url,
    isRelease: Boolean
  )

  object Artifact {

    /**
     * An Artifact instance that is useful for pushing releases to Sonatype.
     */
    val sonatype =
      Artifact(
        location = "https://oss.sonatype.org/",
        isRelease = true
      )
  }

  /**
   * Contains licensing information for the published code.
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
    val noLicense =
      License(
        name = "No License. All Copyrights Reserved",
        location = "http://choosealicense.com/licenses/no-license/",
        distribution = "repo"
      )
  }

}