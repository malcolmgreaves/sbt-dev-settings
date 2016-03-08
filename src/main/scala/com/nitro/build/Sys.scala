package com.nitro.build

import scala.language.postfixOps
import scala.util.Try

object Sys {

  /** Regex matching against mac os x */
  val osxRegex = """(mac)?\ ?os\ ?x?""".r

  /** Regex matching against microsoft windows */
  val windowsRegex = """(microsoft)?\ ?windows.*""".r

  /** The runtime's system-specific identifying string. */
  // Type equivalent to: () => String
  def osStr: String =
    sys.props
      .getOrElse("os_override", System.getProperty("os.name"))
      .toLowerCase

  /**
    * Converts a string into a specific OsType. If the string matching is not
    * able to resolve the input to a known OSType, the the function evaluates
    * to None.
    */
  val osType: String => Option[OsType] =
    s => Try {
      s match {
        case "linux" => Linux
        case osxRegex(_) => Osx
        case windowsRegex(_) => Windows
      }
    }.toOption

  /**
    * Evaluates to an appropriate OsType, calculated internally from the
    * runtime's system and architecture string (from Sys.osStr).
    *
    * Throws a runtime MatchError if the value from osStr is completely
    * unrecognizable.
    */
  def os: OsType =
    osType(osStr) get

}

sealed trait OsType {

  /** Always evaluates to name. */
  final override def toString: String = name

  /** The unique identifying name of platform and architecture. */
  def name: String
}

case object Windows extends OsType {
  val name = "windows-x86"
}

case object Osx extends OsType {
  val name = "macosx-x86_64"
}

case object Linux extends OsType {
  val name = "linux-x86_64"
}
