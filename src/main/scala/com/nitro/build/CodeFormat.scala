package com.nitro.build

/**
 * Settings for automatically formatting Scala code using scalariform.
 */
object CodeFormat {

  import com.typesafe.sbt.SbtScalariform._

  /**
   * Obtain code formatting settings. If formatOnCompile then scalariform will
   * automatically format all source code each time the `compile` task is run.
   * The returned settings will contain this object's `formattingPreferences`.
   */
  def settings(formatOnCompile: Boolean = false) = {

    val baseScalariformFormatSettings =
      if (formatOnCompile)
        scalariformSettings
      else
        defaultScalariformSettings

    baseScalariformFormatSettings ++ Seq(ScalariformKeys.preferences := formattingPreferences)
  }

  import scalariform.formatter.preferences._

  /**
   * The scalariform formatting preferences.
   */
  lazy val formattingPreferences: FormattingPreferences =
    FormattingPreferences()
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(CompactControlReadability, false)
      .setPreference(CompactStringConcatenation, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(FormatXml, true)
      .setPreference(IndentLocalDefs, true)
      .setPreference(IndentPackageBlocks, true)
      .setPreference(IndentSpaces, 2)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
      .setPreference(PreserveDanglingCloseParenthesis, true)
      .setPreference(PreserveSpaceBeforeArguments, false)
      .setPreference(RewriteArrowSymbols, false)
      .setPreference(SpaceBeforeColon, false)
      .setPreference(SpaceInsideBrackets, false)
      .setPreference(SpacesWithinPatternBinders, true)
}