package com.nitro.build

import sbt.Keys._
import sbt.{ Def, Setting }

/**
 * Compilation settings for Scala and Java.
 */
object CompileScalaJava {

  import JvmRuntime.{ JvmVersion, defaultJvmVersion }

  case class Config(
    jvmVer:          JvmVersion,
    fatalWarnings:   Boolean,
    formatOnCompile: Boolean
  )

  object Config {

    val default: Config =
      Config(
        jvmVer = defaultJvmVersion,
        fatalWarnings = true,
        formatOnCompile = false
      )

  }

  private[this] val ver210 = "2.10.5"

  private[this] val ver211 = "2.11.7"

  /**
   * Scala version: 2.10.5
   */
  lazy val scala210 = Seq(scalaVersion := ver210)

  /**
   * Scala version: 2.11.7
   */
  lazy val scala211 = Seq(scalaVersion := ver211)

  /**
   * Scala cross compilation settings: 2.11.7 and 2.10.5
   */
  lazy val crossCompile = Seq(crossScalaVersions := Seq(ver210, ver211))

  /**
   * Settings for doing plugin development (scala 2.10 with base settings).
   */
  def pluginSettings(c: Config = Config.default) =
    settings(c, isScala211 = false) ++ scala210

  /**
   * Settings for doing library or application development
   * (scala 2.11 with base settings and cross-compilation to 2.10).
   */
  def librarySettings(c: Config = Config.default) =
    settings(c, isScala211 = true) ++ scala211 ++ crossCompile

  /**
   * Obtain settings for scalac and javac.
   * Also sets the incremental compilation settings to use name hashing.
   */
  def settings(c: Config, isScala211: Boolean) = {

    val fmt = CodeFormat.settings(c.formatOnCompile)
    val java =
      Seq(
        javacOptions ++= javacSettings(c.jvmVer),
        incOptions := incOptions.value.withNameHashing(nameHashing = true)
      )
    val scala = scalacSettings(c.jvmVer, c.fatalWarnings, logImplicits = false, isScala211)

    fmt ++ java ++ scala
  }

  /**
   * Yield the correct -source and -target settings for the given JVM version.
   */
  def javacSettings(jvmVer: JvmVersion = defaultJvmVersion): Seq[String] =
    Seq(
      "-source", jvmVer.short,
      "-target", jvmVer.short
    )

  /**
   * Yield the correct scalacOptions for the given JVM version, scala version,
   * and whether or not things like fatalWarnings and logging of implicit
   * conversions / classes are done during compilation.
   */
  def scalacSettings(
    jvmVer:        JvmVersion = defaultJvmVersion,
    fatalWarnings: Boolean    = true,
    logImplicits:  Boolean    = false,
    isScala211:    Boolean    = false
  ): Seq[Def.Setting[_]] = {

    val options =
      Seq(
        // warn for deprecations
        "-deprecation",
        // encode source as UTF-8
        "-encoding", "utf8",
        "-feature",
        // Enable advanced Scala language stuff
        "-language:postfixOps",
        "-language:existentials",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-language:experimental.macros",
        // Output optimized bytecode
        "-optimize",
        // Output bytecode specific to the given JVM version
        s"-target:jvm-${jvmVer.short}",
        // Warn on unchecked stuff
        "-unchecked",
        // Run the source code linter
        "-Xlint",
        // Enable future language extensions
        "-Xfuture",
        "-Yno-adapted-args",
        // Emit warnings when dead code is detected
        "-Ywarn-dead-code",
        // Emit a warning if the inferred type of something is `Any`
        "-Ywarn-infer-any",
        // Emit warnings when non-unit values are discarded
        "-Ywarn-value-discard",
        // Emit warnings when things tagged @inline cannot be inlined
        "-Yinline-warnings",
        // Enable all advanced code optimizations
        "-Yopt:_"
      )

    scalacOptions := options
      // turn all warnings into errors
      .addOption(fatalWarnings, "-Xfatal-warnings")
      // during compilation, emit a logging message whenver an
      // implicit conversion or class is used
      .addOption(logImplicits, "-Xlog-implicits")
      // use an optimized bytecode generator
      // only applicable in Scala 2.11 (not available in 2.10, default in 2.12)
      .addOption(isScala211, "-Ybackend:GenBCode")
  }

  /**
   * Used in `scalacSettings`.
   * Makes it easy, syntactically, to conditionally add settings.
   */
  private[this] implicit class AddOption(s: Seq[String]) {
    def addOption(doAdd: Boolean, option: String): Seq[String] =
      if (doAdd)
        s :+ option
      else
        s
  }

}