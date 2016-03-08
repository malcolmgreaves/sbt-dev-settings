package com.nitro.build

import org.scalatest.FunSuite

class SysTest extends FunSuite {

  import SysTest._

  test("String => Option[OsType] works as expected on sample inputs") {
    testdata_str2ostype.foreach {
      case (in, expected) =>
        val actual = Sys.osType(in)
        assert(actual === expected, s"  [failed on input: $in]")
    }
  }

  test("Converting runtime's system string should not be a match error") {
    val _ = Sys.os
    assert(true)
  }

}

object SysTest {

  val testdata_str2ostype: Seq[(String, Option[OsType])] = Seq(
    ("windows", Windows),
    ("liux", None),
    ("linux", Linux),
    ("unix", None),
    ("microsoft windows 10", Windows),
    ("winblows", None),
    ("microsoft", None),
    ("mac os x", Osx),
    ("os x", Osx),
    ("mac os", Osx),
    ("macosx", Osx),
    ("mac", None),
    ("", None),
    (null, None)
  ).map {
    case (in, out) =>
      (
        in,
        out match {
          case None => None
          case x: OsType=> Some(x)
        }
      )
  }

}