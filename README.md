# sbt-dev-settings #
[![Build Status](https://travis-ci.org/malcolmgreaves/sbt-dev-settings.svg?branch=master)](https://travis-ci.org/malcolmgreaves/sbt-dev-settings) [![Codacy Badge](https://api.codacy.com/project/badge/54e884abc2f641b6bac0e22029c3366e)](https://www.codacy.com/app/malcolmgreaves/sbt-dev-settings) [![Join the chat at https://gitter.im/malcolmgreaves/sbt-dev-settings](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/malcolmgreaves/sbt-dev-settings?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![Stories in Ready](https://badge.waffle.io/malcolmgreaves/sbt-dev-settings.png?label=ready&title=Ready)](https://waffle.io/malcolmgreaves/sbt-dev-settings) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.gonitro/sbt-dev-settings/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/com.gonitro/sbt-dev-settings)

### What is this?

**sbt-dev-settings** is a plugin that lets you standardize build settings across many Scala projects. 

### Why use this? 
 
By depending on this plugin, you have access to a standard set of methods that declaritively modify sbt `SettingsKey` instances. You don't have to copy-paste settings and worry about making sure about keeping our build configurations in sync across all libraries. Highlights of this plugin include standardization for:

* `scalarifomSettings`: Code formatting rules through Scalarifom 
* `scalacOptions` and `javacOptions`: Bringing-in lots of useful compilier options
* `javaOptions`: Configuring the Java runtime, bringing optimal options on a per-JVM version basis
* `publishTo`: Tired of messing up your POM and getting rejected from Sonatype? `com.nitro.build.Publish.settings` makes publish configurations a whole lot simpler!
* And more for docker, sbt-git, and common logging and testing dependencies!

### Do I have to learn new sbt commands?

No! (yay!)

This plugin only configures common sbt `SettkingsKey` values. So you can continue using `compile`, `test`, `publish` in sbt just as you used to.

### Examples

#### So how do I use this?

Here's an example for configuring a `build.sbt` file:

    // inclues sbt-dev-settings plugin objects and types
    // * Publish, PublishHelpers
    // * CompileScalaJava
    // * JvmRuntime
    import com.nitro.build._        
    // Pulls in helper types for publishing internally. This includes
    // * SemanticVersion
    // * Repository
    // * ArtifactInfo
    // * License
    import PublishHelpers._   
    
    // // GAV coordinates // //
    
    lazy val pName = "YOUR_PROJECT_NAME"
    lazy val semver = SemanticVersion(MAJOR, MINOR, PATCH, isSnapshot = {true, false})
    organization := "YOUR_GROUP_ID"
    name := pName
    version := semver.toString
    
    // // scala & java settings // //
    
    // There's a lot of room to configure scala, java compilation options.
    // Including incremental compilation options for scala. See `CompileScalaJava.scala`
    // for details.
    // This default configuration does not change incremental compilation options,
    // uses Java 8, has fatal warnings, emits optimized bytecode for Scala, and
    // enables many language features (e.g. macros, higher kinded types, implicits, etc.).
    val devConfig = Config.default
    
    // Use `devConfig` to set the scalacOptions, javacOptions, and incOptions settings.
    scalaVersion := "2.11.7"
    CompileScalaJava.librarySettings(devConfig)
    // `librarySettings` sets up cross compilation for 2.11.7 and 2.10.6.
    // If doing plugin development, use `CompileScalaJava.pluginSettings`,
    // which ensures that we're using only 2.10 and not cross compiling.
    
    // Set the Java runtime options from the same config.
    javaOptions := JvmRuntime.settings(devConfig.jvmVer)
    
    // // Publish Settings // //
    
    // set publishTo with XML generated from this method
    Publish.settings(
      repo = Repository(
       group         = "YOUR_REPO_GROUP_NAME", 
       name          = pName,
       sourceControl = "URL_TO_HOSTED"
      ), 
      // if using Github, consider the simplier method: Repository.github(group,name)
      developers =
        Seq(
          Developer(
           id    = "YOUR_USER_ID",
           name  = "YOUR_NAME",
           email = "YOUR_EMAIL_ADDRESS",
           url   = "URL_TO_YOU"
          )
        ),
      art = // Where you'll be publishing your repo. E.g. sonatype
       ArtifactInfo.sonatype(semver),
      lic = // The license for your code. E.g. Apache 2.0
       License.apache20
    )

### Installation

Add the following to your `project/plugins.sbt` file:

    addSbtPlugin("com.gonitro" % "sbt-dev-settings" % "X.Y.Z")

Where `X.Y.Z` is the most recent one from [sonatype](https://oss.sonatype.org/content/repositories/releases/com/gonitro/sbt-dev-settings_2.10_0.13/).

### Deeper Dive: Build Settings

#### [Compile](https://github.com/malcolmgreaves/sbt-dev-settings/blob/master/src/main/scala/com/nitro/build/CompileScalaJava.scala)

Scala version & compiler settings

#### [Docker](https://github.com/malcolmgreaves/sbt-dev-settings/blob/master/src/main/scala/com/nitro/build/Docker.scala)

Docker publishing settings.

#### [sbt-git](https://github.com/malcolmgreaves/sbt-dev-settings/blob/master/src/main/scala/com/nitro/build/SbtGit.scala)

sbt-git versioning settings.

#### [Publish](https://github.com/malcolmgreaves/sbt-dev-settings/blob/master/src/main/scala/com/nitro/build/Publish.scala)

Publishing to your own Artifactory, Nexus, or other Maven-compatiable jar storage. Depends on types defined in [PublishHelpers](https://github.com/malcolmgreaves/sbt-dev-settings/blob/master/src/main/scala/com/nitro/build/PublishHelpers.scala).

#### [Runtime](https://github.com/malcolmgreaves/sbt-dev-settings/blob/master/src/main/scala/com/nitro/build/JvmRuntime.scala)

Settings for the Java runtime. Includes Java version 6, 7, and 8.

#### [Format](https://github.com/malcolmgreaves/sbt-dev-settings/blob/master/src/main/scala/com/nitro/build/CodeFormat.scala)

Code formatting settings using scalarifom.

#### [Dependencies](https://github.com/malcolmgreaves/sbt-dev-settings/blob/master/src/main/scala/com/nitro/build/Dependencies.scala)

Common dependencies for fundamental things, e.g. logging and testing.

## How Do I Contribute?

We welcome all pull requests! We <3 this software; we want it to be used and be useful. Please submit a PR for any changes to kick-off a code review to reach consensus.

### License

Copyright 2015- Malcolm Greaves

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
