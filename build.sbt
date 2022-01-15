/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

import ScalaVersions._

ThisBuild / organization := "com.github.takapi327"
ThisBuild / startYear    := Some(2022)

lazy val LepusFramework = Project("Lepus-Framework", file("."))
  .settings(
    scalaVersion := sys.props.get("scala.version").getOrElse(scala213),
  )
