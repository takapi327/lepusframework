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

lazy val SbtPluginProject = Project("Sbt-Plugin", file("development/sbt-plugin"))
  .enablePlugins(SbtPlugin)
  .settings(
    scalaVersion       := scala212,
    crossScalaVersions := Seq(scala212),
    libraryDependencies ++= Seq(
      Defaults.sbtPluginExtra(
        "com.github.sbt" % "sbt-native-packager" % "1.9.7",
        CrossVersion.binarySbtVersion("1.5.5"),
        CrossVersion.binaryScalaVersion(scala212)
      )
    ),
    (Compile / sourceGenerators) += Def.task {
      Generator.lepusVersion(
        version      = version.value,
        scalaVersion = (LepusProject / scalaVersion).value,
        sbtVersion   = sbtVersion.value,
        dir          = (Compile / sourceManaged).value
      )
    }.taskValue
  )

lazy val LepusFramework = Project("Lepus-Framework", file("."))
  .settings(
    scalaVersion := sys.props.get("scala.version").getOrElse(scala213),
    commonSettings
  )
