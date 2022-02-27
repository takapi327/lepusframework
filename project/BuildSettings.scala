/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

import sbt._
import sbt.Keys._

object BuildSettings {

  val baseScalaSettings = Seq(
    "-Xfatal-warnings",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-encoding",
    "utf8",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions"
  )

  /** These settings are used by all projects. */
  def commonSettings: Seq[Setting[_]] = Def.settings(
    organization := "com.github.takapi327",
    startYear    := Some(2022),
    licenses     := Seq("MIT" -> url("https://img.shields.io/badge/license-MIT-green")),
    Test / fork  := true,
    run  / fork  := true,
    scalacOptions ++= baseScalaSettings
  )
}
