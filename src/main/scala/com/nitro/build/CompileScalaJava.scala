package com.nitro.build

import sbt.Keys._
import sbt.{ SettingKey, Def, Setting }

/**
 * Compilation settings for Scala and Java.
 */
object CompileScalaJava {

  import JvmRuntime.{ JvmVersion, defaultJvmVersion }

  case class ScalaConfig(
    fatalWarnings: Boolean,
    logImplicits:  Boolean,
    optimize:      Boolean,
    crossCompile:  Seq[String],
    inlineWarn:    Boolean,
    genBBackend:   Boolean
  )

  object ScalaConfig {

    val cross = Seq("2.10.6", "2.11.7")

    val default: ScalaConfig =
      ScalaConfig(
        fatalWarnings = true,
        logImplicits = false,
        optimize = true,
        crossCompile = cross,
        inlineWarn = true,
        genBBackend = true
      )
  }

  case class IncConfig(
    onMacroDef: Boolean,
    nameHash:   Boolean
  )

  object IncConfig {

    val default: IncConfig =
      IncConfig(
        onMacroDef = true,
        nameHash = true
      )
  }

  case class Config(
    jvmVer:                        JvmVersion,
    formatOnCompile:               Boolean,
    scala:                         ScalaConfig,
    inc:                           Option[IncConfig],
    compileOptionOverrideToSimple: Boolean           = false
  )

  object Config {

    val default: Config =
      Config(
        jvmVer = defaultJvmVersion,
        formatOnCompile = false,
        scala = ScalaConfig.default,
        inc = None
      )

    def ensureStrict(c: Config = default) =
      c.copy(scala = c.scala.copy(fatalWarnings = true))

    def ensureJvm7(c: Config = default) =
      c.copy(jvmVer = JvmRuntime.Jvm7)

    def overrideAllCompileOptionsToSimple(c: Config = default) =
      c.copy(compileOptionOverrideToSimple = true)

    val spark = ensureJvm7(default)

    val plugin =
      default.copy(scala =
        default.scala.copy(crossCompile =
          Seq.empty[String]))

  }

  /**
   * [mutation!] Sets scalaVersion to 2.10.6
   * Ignores any crossCompile settings in the input Config, c.
   */
  def pluginSettings(c: Config = Config.plugin) = {
    scalaVersion := "2.10.6"
    val updatedC = c.copy(scala =
      c.scala.copy(crossCompile = Seq.empty[String]))
    settings(updatedC) ++ Seq(sbtPlugin := true)
  }

  /**
   * Settings for doing library or application development
   * (scala 2.11 with base settings and cross-compilation to 2.10).
   *
   * Note. Unlike plugin(), this does not mutate scalaVersion. It does
   * make sure that c.scala.crossCompile == (2.10.6, 2.11.7)
   */
  def librarySettings(c: Config = Config.default) = {
    val updatedC = c.copy(scala =
      c.scala.copy(crossCompile = ScalaConfig.cross))
    settings(updatedC) ++ Seq(crossScalaVersions := c.scala.crossCompile)
  }

  /**
   * Obtain settings for scalac and javac.
   * Also sets the incremental compilation settings to use name hashing.
   */
  def settings(c: Config) = {

    val fmt =
      CodeFormat.settings(c.formatOnCompile)

    val incCompile =
      if (c.compileOptionOverrideToSimple || c.inc.isEmpty)
        Seq.empty[Def.Setting[_]]

      else
        Seq(incOptions := incOptions.value
          .withRecompileOnMacroDef(c.inc.get.onMacroDef)
          .withNameHashing(c.inc.get.nameHash))

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
          "-Ywarn-value-discard"
        )

      scalacOptions := options
        // Emit a warning if the inferred type of something is `Any`
        .addOption(isScala211(scalaVersion.value), "-Ywarn-infer-any")
        // Output optimized bytecode (include all under -Yopt iff 2.11.x)
        .addOption(c.scala.optimize && isScala211(scalaVersion.value), "-optimize", "-Yopt:_")
        // Output optimized bytecode (iff using 2.10.x)
        .addOption(c.scala.optimize && !isScala211(scalaVersion.value), "-optimize")
        // turn all warnings into errors
        .addOption(c.scala.fatalWarnings, "-Xfatal-warnings")
        // during compilation, emit a logging message whenver an
        // implicit conversion or class is used
        .addOption(c.scala.logImplicits, "-Xlog-implicits")
        // use an optimized bytecode generator
        // only applicable in Scala 2.11 (not available in 2.10, default in 2.12)
        .addOption(c.scala.genBBackend && isScala211(scalaVersion.value), "-Ybackend:GenBCode")
        // Emit warnings when things tagged @inline cannot be inlined
        .addOption(c.scala.inlineWarn, "-Yinline-warnings")
    }

  private[this] def isScala211(v: String): Boolean =
    v.startsWith("2.11.")

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