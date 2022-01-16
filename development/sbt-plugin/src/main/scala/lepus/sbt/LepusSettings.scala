/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.sbt

import scala.Console._

import sbt._
import sbt.Keys._
import sbt.Path._

import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._

import lepus.core.LepusVersion
import LepusImport.LepusKeys._
import LepusInternalKeys._

object LepusSettings {

  lazy val serverSettings = Def.settings(
    onLoadMessage := {
      """|
         |       __      _______  _____   __   __ _______
         |      / /     / ___  / / ___ \ / /  / // _____/
         |     / /     / /__/ / / /  / // /  / // /____
         |    / /     / _____/ / /  / // /  / //____  /
         |   / /____ / /____  / /__/ // /__/ / ____/ /
         |  /______/ \___,_/ / .____/ \___,_//______/
         |                  / /
         |                 /_/
         |""".stripMargin.lines.map(v => BLUE + v + RESET).mkString("\n") +
        s"""
           |
           |  Version Information
           |    - Lepus ${LepusVersion.current}
           |    - Java  ${System.getProperty("java.version")}
           |    - Scala ${LepusVersion.scalaVersion}
           |    - Sbt   ${LepusVersion.sbtVersion}
           |
           |""".stripMargin
    },
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
    defaultPort := 5555,
    defaultAddress := "0.0.0.0",
    lepusDependencyClasspath := (Runtime / externalDependencyClasspath).value,
    baseClassloader := LepusCommands.baseClassloaderTask.value,
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
}
