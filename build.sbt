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
ThisBuild / crossScalaVersions         := Seq(scala3, scala212)
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
    scalas = List(scala3, scala212),
    javas  = List(JavaSpec.temurin(java11)),
  ),
  WorkflowJob(
    "sbtScripted",
    "sbt scripted",
    githubWorkflowJobSetup.value.toList ::: List(
      WorkflowStep.Run(
        List("sbt +publishLocal"),
        name = Some("sbt publishLocal"),
      ),
      WorkflowStep.Run(
        List("sbt scripted"),
        name = Some("sbt scripted"),
      )
    ),
    scalas = List(scala3),
    javas  = List(JavaSpec.temurin(java11)),
  )
)

// Project settings
lazy val LepusProject = LepusSbtProject("Lepus", "core/lepus")
  .settings(scalaVersion := sys.props.get("scala.version").getOrElse(scala3))
  .settings(
    libraryDependencies ++= Seq(
      cats,
      typesafeConfig,
    ) ++ specs2Deps
  )

lazy val LepusRouterProject = LepusSbtProject("Lepus-Router", "core/lepus-router")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= routerDependencies ++ specs2Deps ++ Seq(
    magnolia3
  ))
  .dependsOn(LepusProject, LepusLoggerProject)
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)

lazy val LepusLogbackProject = LepusSbtProject("Lepus-Logback", "core/lepus-logback")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies += logback)

lazy val LepusLoggerProject = LepusSbtProject("Lepus-Logger", "core/lepus-logger")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= Seq(catsEffect) ++ specs2Deps)

lazy val LepusServerProject = LepusSbtProject("Lepus-Server", "development/lepus-server")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(
    (Compile / unmanagedSourceDirectories) += {
      val suffix = CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((x, y)) => s"$x.$y"
        case None         => scalaBinaryVersion.value
      }
      (Compile / sourceDirectory).value / s"scala-$suffix"
    }
  )
  .dependsOn(LepusRouterProject)

lazy val LepusSwaggerProject = LepusSbtProject("Lepus-Swagger", "development/lepus-swagger")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= swaggerDependencies ++ specs2Deps)
  .dependsOn(LepusRouterProject)

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
  LepusRouterProject,
  LepusLogbackProject,
  LepusLoggerProject
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
