/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

import ScalaVersions._
import BuildSettings._
import Dependencies._

lazy val LepusProject = Project("Lepus", file("core/lepus"))
  .settings(
    scalaVersion       := sys.props.get("scala.version").getOrElse(ScalaVersions.scala213),
    crossScalaVersions := Seq(scalaVersion.value),
    commonSettings,
    scalacOptions += "-target:jvm-1.8",
    libraryDependencies ++= Seq(
      cats,
      typesafeConfig,
    ) ++ specs2Deps.map(_ % Test)
  )

lazy val LepusFramework = Project("Lepus-Framework", file("."))
  .settings(
    scalaVersion := sys.props.get("scala.version").getOrElse(scala213),
    commonSettings
  )
