/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

import ScalaVersions._
import JavaVersions._
import BuildSettings._
import Dependencies._

// Global settings
ThisBuild / crossScalaVersions         := Seq(scala3, scala213, scala212)
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin(java11), JavaSpec.temurin(java8))

ThisBuild / githubWorkflowAddedJobs ++= Seq(
  WorkflowJob(
    "scalafmt",
    "Scalafmt",
    githubWorkflowJobSetup.value.toList ::: List(
      WorkflowStep.Run(
        List("sbt scalafmtCheck"),
        name = Some("Scalafmt check"),
      )
    ),
    scalas = List(scala3, scala213, scala212),
    javas  = List(JavaSpec.temurin(java11)),
  )
)

// Project settings
lazy val LepusProject = LepusSbtProject("Lepus", "core/lepus")
  .settings(scalaVersion := sys.props.get("scala.version").getOrElse(scala213))
  .settings(
    libraryDependencies ++= Seq(
      cats,
      typesafeConfig,
    ) ++ specs2Deps(scalaVersion.value)
  )

lazy val LepusRouterProject = LepusSbtProject("Lepus-Router", "core/lepus-router")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= routerDependencies ++ {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) => Seq(magnolia3)
      case _            => Seq(magnolia2, reflect)
    }
  } ++ specs2Deps(scalaVersion.value))

lazy val LepusServerProject = LepusSbtProject("Lepus-Server", "development/lepus-server")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= serverDependencies)
  .settings(
    (Compile / unmanagedSourceDirectories) += {
      val suffix = CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((x, y)) => s"$x.$y"
        case None         => scalaBinaryVersion.value
      }
      (Compile / sourceDirectory).value / s"scala-$suffix"
    }
  )
  .dependsOn(LepusProject, LepusRouterProject)

lazy val LepusSwaggerProject = LepusSbtProject("Lepus-Swagger", "development/lepus-swagger")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= swaggerDependencies ++ specs2Deps(scalaVersion.value))
  .dependsOn(LepusProject, LepusRouterProject)

lazy val SbtPluginProject = LepusSbtPluginProject("Sbt-Plugin", "development/sbt-plugin")
  .settings(
    libraryDependencies ++= Seq(
      Defaults.sbtPluginExtra(
        "com.github.sbt" % "sbt-native-packager" % "1.9.7",
        CrossVersion.binarySbtVersion(sbtVersion.value),
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

lazy val SbtScriptedToolsProject = LepusSbtPluginProject("Sbt-Scripted-Tools", "development/sbt-scripted-tools")
  .dependsOn(SbtPluginProject)

lazy val userProjects = Seq[ProjectReference](
  LepusProject,
  LepusRouterProject
)

lazy val nonUserProjects = Seq[ProjectReference](
  SbtPluginProject,
  SbtScriptedToolsProject,
  LepusServerProject,
  LepusSwaggerProject
)

lazy val LepusFramework = Project("Lepus-Framework", file("."))
  .settings(
    scalaVersion       := (LepusProject / scalaVersion).value,
    crossScalaVersions := Nil
  )
  .settings(publish / skip := true)
  .settings(commonSettings: _*)
  .aggregate((userProjects ++ nonUserProjects): _*)
