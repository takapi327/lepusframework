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

import ReleaseTransformations._
lazy val publishSettings = Seq(
  publishTo := Some("Lepus Maven" at "s3://com.github.takapi327.s3-ap-northeast-1.amazonaws.com/lepus/"),
  (Compile / packageDoc) / publishArtifact := false,
  (Compile / packageSrc) / publishArtifact := false,
  releaseProcess := Seq[ReleaseStep](
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)

// Project settings
lazy val LepusProject = Project("Lepus", file("core/lepus"))
  .settings(scalaVersion := sys.props.get("scala.version").getOrElse(scala213))
  .settings(
    libraryDependencies ++= Seq(
      cats,
      typesafeConfig,
    ) ++ specs2Deps(scalaVersion.value)
  )
  .settings(multiVersionSettings: _*)
  .settings(publishSettings: _*)

lazy val LepusRouterProject = Project("Lepus-Router", file("core/lepus-router"))
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= routerDependencies ++ {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) => Seq(magnolia3)
      case _            => Seq(magnolia2, reflect)
    }
  } ++ specs2Deps(scalaVersion.value))
  .settings(multiVersionSettings: _*)
  .settings(publishSettings: _*)

lazy val LepusServerProject = Project("Lepus-Server", file("development/lepus-server"))
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
  .settings(multiVersionSettings: _*)
  .settings(publishSettings: _*)
  .dependsOn(LepusProject, LepusRouterProject)

lazy val LepusSwaggerProject = Project("Lepus-Swagger", file("development/lepus-swagger"))
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= swaggerDependencies ++ specs2Deps(scalaVersion.value))
  .settings(multiVersionSettings: _*)
  .settings(publishSettings: _*)
  .dependsOn(LepusProject, LepusRouterProject)

lazy val SbtPluginProject = Project("Sbt-Plugin", file("development/sbt-plugin"))
  .enablePlugins(SbtPlugin)
  .settings(
    scalaVersion       := scala212,
    crossScalaVersions := Seq(scala212),
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
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)

lazy val userProjects = Seq[ProjectReference](
  LepusProject,
  LepusRouterProject
)

lazy val nonUserProjects = Seq[ProjectReference](
  SbtPluginProject,
  LepusServerProject,
  LepusSwaggerProject
)

lazy val LepusFramework = Project("Lepus-Framework", file("."))
  .settings(
    scalaVersion       := (LepusProject / scalaVersion).value,
    crossScalaVersions := Nil,
  )
  .settings(publish / skip := true)
  .settings(commonSettings: _*)
  .aggregate((userProjects ++ nonUserProjects): _*)
