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

object LepusSettings {

  lazy val serverSettings = Def.settings(
    onLoadMessage := {
      """|
         |      __      _______  ____    __  __  ______
         |     / /     / ___  / / __ \  / / / / / ___ /
         |    / /     / /__/ / / / / / / / / / / /___
         |   / /____ /  ____/ / /_/ / / /_/ /  ___/ /
         |  /______/ \___,_/ / .___/  \__,_/ /_____/
         |                  /_/
         |
         |""".stripMargin.linesIterator.map(v => BLUE + v + RESET).mkString("\n") +
        s"""
           |
           |  Version Information
           |    - Lepus ${ LepusVersion.current }
           |    - Java  ${ System.getProperty("java.version") }
           |
           |""".stripMargin
    },
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
    libraryDependencies ++= Seq(lepusServer, lepusRouter),
    Compile / run / mainClass   := Some("lepus.server.LepusServer"),
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
    commands += LepusCommands.swaggerCommand
  )
}
