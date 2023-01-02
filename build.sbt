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
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin(java11))

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

// Core projects
lazy val LepusProject = LepusSbtProject("Lepus", "core/lepus")
  .settings(scalaVersion := sys.props.get("scala.version").getOrElse(scala3))
  .settings(
    libraryDependencies ++= Seq(
      cats,
      typesafeConfig,
      magnolia3
    ) ++ specs2Deps
  )

lazy val LepusGuiceProject = LepusSbtProject("Lepus-Guice", "core/lepus-guice")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= Seq(catsEffect, guice) ++ specs2Deps)
  .dependsOn(LepusProject)

lazy val LepusAppProject = LepusSbtProject("Lepus-App", "core/lepus-app")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= Seq(http4sDsl, mapRef))
  .dependsOn(LepusProject, LepusGuiceProject)

lazy val LepusServerProject = LepusSbtProject("Lepus-Server", "core/lepus-server")
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
  .settings(libraryDependencies ++= http4sEmber ++ specs2Deps)
  .dependsOn(LepusAppProject)

lazy val SbtPluginProject = LepusSbtPluginProject("Sbt-Plugin", "core/sbt-plugin")
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

// Module projects
lazy val LepusLogbackProject = LepusSbtProject("Lepus-Logback", "modules/lepus-logback")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies += logback)

lazy val LepusLoggerProject = LepusSbtProject("Lepus-Logger", "modules/lepus-logger")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= Seq(
    catsEffect,
    "io.circe" %% "circe-core" % circeVersion
  ) ++ specs2Deps)

lazy val LepusDatabaseProject = LepusSbtProject("Lepus-Database", "modules/lepus-database")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= specs2Deps)
  .dependsOn(LepusProject, LepusLoggerProject)

lazy val LepusHikariProject = LepusSbtProject("Lepus-Hikari", "modules/lepus-hikari")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= Seq(hikariCP) ++ specs2Deps)
  .dependsOn(LepusDatabaseProject)

lazy val LepusDoobieProject = LepusSbtProject("Lepus-doobie", "modules/lepus-doobie")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= Seq(
    doobie,
    "org.specs2" %% "specs2-core" % "4.15.0"
  ) ++ specs2Deps)
  .dependsOn(LepusHikariProject, LepusGuiceProject)

lazy val LepusRouterProject = LepusSbtProject("Lepus-Router", "modules/lepus-router")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= routerDependencies ++ specs2Deps)
  .dependsOn(LepusAppProject)
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)

lazy val LepusSwaggerProject = LepusSbtProject("Lepus-Swagger", "modules/lepus-swagger")
  .settings(scalaVersion := (LepusProject / scalaVersion).value)
  .settings(libraryDependencies ++= swaggerDependencies ++ specs2Deps)
  .dependsOn(LepusRouterProject)

// Development projects
lazy val SbtScriptedToolsProject = LepusSbtPluginProject("Sbt-Scripted-Tools", "development/sbt-scripted-tools")
  .dependsOn(SbtPluginProject)

lazy val coreProjects: Seq[ProjectReference] = Seq(
  LepusProject,
  LepusGuiceProject,
  LepusAppProject,
  LepusServerProject,
  SbtPluginProject
)

lazy val moduleProjects: Seq[ProjectReference] = Seq(
  LepusLogbackProject,
  LepusLoggerProject,
  LepusDatabaseProject,
  LepusHikariProject,
  LepusDoobieProject,
  LepusRouterProject,
  LepusSwaggerProject
)

lazy val developmentProjects: Seq[ProjectReference] = Seq(
  SbtScriptedToolsProject
)

lazy val LepusFramework = Project("Lepus-Framework", file("."))
  .settings(
    scalaVersion       := (LepusProject / scalaVersion).value,
    crossScalaVersions := Nil
  )
  .settings(publish / skip := true)
  .settings(commonSettings: _*)
  .aggregate((coreProjects ++ moduleProjects ++ developmentProjects): _*)
