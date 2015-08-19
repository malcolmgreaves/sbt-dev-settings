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
    formatOnCompile: Boolean,
    scala:           ScalaConfig,
    inc:             IncConfig
  )

  case class ScalaConfig(
    fatalWarnings: Boolean,
    isScala211:    Boolean,
    logImplicits:  Boolean,
    optimize:      Boolean
  )

  case class IncConfig(
    onMacroDef: Boolean,
    nameHash:   Boolean
  )

  object Config {

    val default: Config =
      Config(
        jvmVer = defaultJvmVersion,
        formatOnCompile = false,
        scala =
          ScalaConfig(
            fatalWarnings = true,
            logImplicits = false,
            isScala211 = true,
            optimize = true
          ),
        inc =
          IncConfig(
            onMacroDef = true,
            nameHash = true
          )
      )

    val strict: Config =
      default.copy(scala = default.scala.copy(fatalWarnings = true))

    val spark: Config =
      default.copy(jvmVer = JvmRuntime.Jvm7)

    val plugin: Config =
      default.copy(scala = default.scala.copy(isScala211 = false))

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
  def pluginSettings(c: Config = Config.plugin) =
    settings(c) ++ scala210

  /**
   * Settings for doing library or application development
   * (scala 2.11 with base settings and cross-compilation to 2.10).
   */
  def librarySettings(c: Config = Config.default) =
    settings(c) ++ {
      if (c.scala.isScala211)
        scala211 ++ crossCompile
      else
        scala210
    }

  /**
   * Obtain settings for scalac and javac.
   * Also sets the incremental compilation settings to use name hashing.
   */
  def settings(c: Config) = {

    val fmt =
      CodeFormat.settings(c.formatOnCompile)

    val java =
      Seq(javacOptions ++= javacSettings(c))

    val incCompile =
      Seq(incOptions := incOptions.value
        .withRecompileOnMacroDef(c.inc.onMacroDef)
        .withNameHashing(c.inc.nameHash))

    val scala =
      scalacSettings(c)

    // use all !

    fmt ++ java ++ incCompile ++ scala
  }

  /**
   * Yield the correct -source and -target settings for the given JVM version.
   */
  def javacSettings(c: Config = Config.default): Seq[String] =
    Seq(
      "-source", c.jvmVer.short,
      "-target", c.jvmVer.short
    )

  /**
   * Yield the correct scalacOptions for the given JVM version, scala version,
   * and whether or not things like fatalWarnings and logging of implicit
   * conversions / classes are done during compilation.
   */
  def scalacSettings(c: Config = Config.default): Seq[Def.Setting[_]] = {

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
        // Output bytecode specific to the given JVM version
        s"-target:jvm-${c.jvmVer.short}",
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
        "-Yinline-warnings"
      )

    scalacOptions := options
      // Output optimized bytecode
      .addOption(c.scala.optimize, "-optimize", "-Yopt:_")
      // turn all warnings into errors
      .addOption(c.scala.fatalWarnings, "-Xfatal-warnings")
      // during compilation, emit a logging message whenver an
      // implicit conversion or class is used
      .addOption(c.scala.logImplicits, "-Xlog-implicits")
      // use an optimized bytecode generator
      // only applicable in Scala 2.11 (not available in 2.10, default in 2.12)
      .addOption(c.scala.isScala211, "-Ybackend:GenBCode")
  }

  /**
   * Used in `scalacSettings`.
   * Makes it easy, syntactically, to conditionally add settings.
   */
  private[this] implicit class AddOption(s: Seq[String]) {
    def addOption(doAdd: Boolean, options: String*): Seq[String] =
      if (doAdd)
        s ++ options
      else
        s
  }

}