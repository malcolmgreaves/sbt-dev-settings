package com.nitro.build

import sbt.Keys._
import sbt.{ Def, Setting }

/**
 * Compilation settings for Scala and Java.
 */
object CompileScalaJava {

  import JvmRuntime.{ JvmVersion, defaultJvmVersion }

  case class ScalaConfig(
    fatalWarnings: Boolean,
    isScala211:    Boolean,
    logImplicits:  Boolean,
    optimize:      Boolean
  )

  object ScalaConfig {

    val default: ScalaConfig =
      ScalaConfig(
        fatalWarnings = true,
        isScala211 = true,
        logImplicits = true,
        optimize = true
      )

    def ensure211x(c: ScalaConfig = default) =
      c.copy(isScala211 = true)

    def ensure210x(c: ScalaConfig = default) =
      c.copy(isScala211 = false)

  }

  case class IncConfig(
    onMacroDef: Boolean,
    nameHash:   Boolean
  )

  case class Config(
    jvmVer:                        JvmVersion,
    formatOnCompile:               Boolean,
    scala:                         ScalaConfig,
    inc:                           IncConfig,
    compileOptionOverrideToSimple: Boolean     = false
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

    def ensureStrict(c: Config = default) =
      c.copy(scala = c.scala.copy(fatalWarnings = true))

    def ensureJvm7(c: Config = default) =
      c.copy(jvmVer = JvmRuntime.Jvm7)

    def overrideAllCompileOptionsToSimple(c: Config = default) =
      c.copy(compileOptionOverrideToSimple = true)

    val spark = ensureJvm7()

    val plugin = default.copy(scala = ScalaConfig.ensure210x())

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

    val incCompile =
      if (c.compileOptionOverrideToSimple)
        Seq.empty[Def.Setting[_]]

      else
        Seq(incOptions := incOptions.value
          .withRecompileOnMacroDef(c.inc.onMacroDef)
          .withNameHashing(c.inc.nameHash))

    // use all !

    fmt ++ javacSettings(c) ++ incCompile ++ scalacSettings(c)
  }

  /**
   * Yield the correct -source and -target settings for the given JVM version.
   */
  def javacSettings(c: Config = Config.default): Seq[Def.Setting[_]] =
    if (c.compileOptionOverrideToSimple)
      Seq.empty[Def.Setting[_]]

    else
      Seq(
        javacOptions ++= Seq(
          "-source", c.jvmVer.short,
          "-target", c.jvmVer.short
        )
      )

  /**
   * Yield the correct scalacOptions for the given JVM version, scala version,
   * and whether or not things like fatalWarnings and logging of implicit
   * conversions / classes are done during compilation.
   */
  def scalacSettings(c: Config = Config.default): Seq[Def.Setting[_]] =
    if (c.compileOptionOverrideToSimple)
      scalacOptions := Seq.empty[String]

    else {

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
          // Emit warnings when non-unit values are discarded
          "-Ywarn-value-discard",
          // Emit warnings when things tagged @inline cannot be inlined
          "-Yinline-warnings"
        )

      scalacOptions := options
        // Emit a warning if the inferred type of something is `Any`
        .addOption(c.scala.isScala211, "-Ywarn-infer-any")
        // Output optimized bytecode (include all under -Yopt iff 2.11.x)
        .addOption(c.scala.optimize && c.scala.isScala211, "-optimize", "-Yopt:_")
        // Output optimized bytecode (iff using 2.10.x)
        .addOption(c.scala.optimize && !c.scala.isScala211, "-optimize")
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