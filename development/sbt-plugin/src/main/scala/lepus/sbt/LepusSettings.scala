/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.sbt

import scala.Console._

import sbt._
import sbt.Keys._
import sbt.Path._

import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._

import lepus.core.LepusVersion
import LepusImport._
import LepusInternalKeys._
import LepusSwaggerImport._

object LepusSettings {

  lazy val serverSettings = Def.settings(
    onLoadMessage := {
      val javaVersion = System.getProperty("java.version")
      """|
         |      __      ______  ____    __  __  ______
         |     / /     / __  / / __ \  / / / / / ___ /
         |    / /     / /_/ / / / / / / / / / / /___
         |   / /____ /  ___/ / /_/ / / /_/ /  ___/ /
         |  /______/ \__,_/ / .___/  \__,_/ /_____/
         |                 /_/
         |
         |""".stripMargin.linesIterator.map(v => BLUE + v + RESET).mkString("\n") +
        s"""
           |
           |  Version Information
           |    - Lepus ${ LepusVersion.current }
           |    - Java  $javaVersion
           |    - Scala ${ (Compile / scalaVersion).value }
           |
           |""".stripMargin +
        (if (javaVersion != "11" && !javaVersion.split('.').headOption.contains("11"))
           s"""
             |!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
             |  Java version is $javaVersion. Lepus supports only 11.
             |!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
             |
             |""".stripMargin.linesIterator.map(v => YELLOW + v + RESET).mkString("\n")
         else "") +
        (CrossVersion.partialVersion((Compile / scalaVersion).value) match {
          case Some((2, _)) =>
            s"""
              |!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
              |  Scala version is ${ (Compile / scalaVersion).value }. Lepus supports only 3.x.x
              |!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
              |
              |If you wish to use Scala2 series, please use Lepus Framework v0.2.x or earlier.
              |
              |""".stripMargin
          case _ => ""
        })
    },
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
    resolvers += "Lepus Maven" at "s3://com.github.takapi327.s3-ap-northeast-1.amazonaws.com/lepus/",
    libraryDependencies ++= Seq(lepusServer),
    Compile / run / mainClass := Some("lepus.server.LepusServer"),
    appProcessForkOptions := {
      taskTemporaryDirectory.value
      ForkOptions(
        javaHome         = javaHome.value,
        outputStrategy   = outputStrategy.value,
        bootJars         = Vector.empty[File],
        workingDirectory = Option(baseDirectory.value),
        runJVMOptions    = javaOptions.value.toVector,
        connectInput     = false,
        envVars          = envVars.value
      )
    },
    background := Def
      .inputTask {
        Actions.startBackground(
          projectRef = thisProjectRef.value,
          options    = forkOptions.value,
          mainClass  = (Compile / run / mainClass).value,
          classpath  = (Runtime / fullClasspath).value
        )
      }
      .dependsOn(Compile / products)
      .evaluated,
    stop                        := Actions.stopApp(thisProjectRef.value),
    lepusDependencyClasspath    := (Runtime / externalDependencyClasspath).value,
    Compile / resourceDirectory := baseDirectory(_ / "conf").value,
    externalizedResources := {
      val resourceDirectories = (Compile / unmanagedResourceDirectories).value
      ((Compile / unmanagedResources).value --- resourceDirectories)
        .pair(relativeTo(resourceDirectories) | flat)
    },
    Universal / mappings ++= {
      val resourceMappings = (Compile / externalizedResources).value
      resourceMappings.map {
        case (resource, path) => resource -> ("conf/" + path)
      }
    }
  )

  lazy val swaggerSettings = Def.settings(
    libraryDependencies += lepusSwagger,
    baseClassloader := LepusCommands.baseClassloaderTask.value,
    (Compile / sourceGenerators) += LepusGenerator.generateSwagger.taskValue,
    commands += LepusCommands.swaggerCommand,
    swaggerTitle   := None,
    swaggerVersion := None
  )
}
