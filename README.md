# sbt--dev-settings #
[![Build Status](https://travis-ci.org/malcolmgreaves/sbt-dev-settings.svg?branch=master)](https://travis-ci.org/malcolmgreaves/sbt-dev-settings)

##### ALPHA WARNING
This work is ongoing. This codebase is continually evolving; APIs break constantly. It should not be relied upon (yet!).

### What is this?

**sbt-dev-settings** is a plugin that lets us standardize build settings (code formatting rules, compilation, runtime, publishing, ) across many Scala repos.

### Why use this? 
 
You don't have to copy-paste settings and worry about making sure about keeping our build configurations in sync across all libraries.  Things like publishing & dockerizing are managed for you!

### Do I have to learn new SBT commands?

No!

You can continue using `compile`, `test`, `publishLocal` in sbt just as you used to.

### So how do I use this?

TODO

## Installation

Add the following to your `project/plugins.sbt` file:

    addSbtPlugin("com.gonitro" % "sbt-dev-settings" % "X.Y.Z")

## Usage

### Common configurations

#### Build & publishing configuration

##### Library

TODO

##### Docker Container

TODO

##### SBT Plugin

TODO

### What you still have to provide

+ GroupId

    organization := "my-org"

+ Project name

    name := "my-project"

+ Version

    version := "0.0.1"

+ Your own dependencies

    libraryDependencies ++= Seq( ... )

### Build Settings

#### [Compile](??? todo ???)

Scala version & compiler settings

#### [Docker](??? todo ???)

Docker publishing settings.

#### [sbt-git](???)

sbt-git versioning settings.

#### [Publish](??? todo ???)

Publishing to your own Artifactory, Nexus, or other Maven-compatiable jar storage.

## How Do I Contribute?

We welcome all pull requests!
