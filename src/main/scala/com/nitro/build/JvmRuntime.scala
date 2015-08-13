package com.nitro.build

/**
 * Settings that configure the Java Virtual Machine runtime.
 */
object JvmRuntime {

  sealed trait JvmVersion {
    def short: String
  }

  case object Jvm6 extends JvmVersion {
    override def short = "1.6"
  }

  case object Jvm7 extends JvmVersion {
    override def short = "1.7"
  }

  case object Jvm8 extends JvmVersion {
    override def short = "1.8"
  }

  /**
   * Default version is JVM 8.
   */
  lazy val defaultJvmVersion = Jvm8

  /**
   * Obtain the settings for a given JVM version.
   */
  def settings(jvm: JvmVersion = defaultJvmVersion): Seq[String] =
    baseJvmSettings ++ additionalJvmSettings(jvm)

  /**
   * Settings for all JVM versions. Specifies: -server and -XX:+AggressiveOpts
   */
  lazy val baseJvmSettings = Seq("-server", "-XX:+AggressiveOpts")

  /**
   * The additional settings specific to each JVM version. The details are:
   *
   * JVM 6: Nothing
   * JVM 7: -XX:+UseG1GC and -XX:+TieredCompilation
   * JVM 8: -XX:+UseG1GC and -XX:+TieredCompilation and -XX:+UseStringDeduplication
   */
  def additionalJvmSettings(jvm: JvmVersion): Seq[String] =
    jvm match {
      case Jvm6 => Seq.empty[String]
      case Jvm7 => Seq("-XX:+UseG1GC", "-XX:+TieredCompilation")
      case Jvm8 => Seq("-XX:+UseG1GC", "-XX:+TieredCompilation", "-XX:+UseStringDeduplication")
    }

}