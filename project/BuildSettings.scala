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

  /**
   * Change SourceDir according to Scala version
   *
   * @param sourceDir
   * Directory under src of each project
   * @param scalaVersion
   * Scala version to be used in each project
   * @return
   * Returns the directory corresponding to the version
   */
  private def changeSourceDirByVersion(sourceDir: File, scalaVersion: String): List[File] =
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((3, _)) => List(sourceDir / "scala3")
      case Some((2, _)) => List(sourceDir / "scala2.13")
      case _            => List(sourceDir / "scala2.13")
    }

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
