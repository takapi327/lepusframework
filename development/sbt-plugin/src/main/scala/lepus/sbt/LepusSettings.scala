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
           |
           |""".stripMargin +
        (if (javaVersion != "1.8" && javaVersion != "11")
          s"""
             |!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
             |  Java version is $javaVersion. Lepus supports only 8 or 11.
             |!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
             |
             |""".stripMargin.linesIterator.map(v => YELLOW + v + RESET).mkString("\n")
        else "")
    },
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
    libraryDependencies ++= Seq(lepusServer),
    Compile / run / mainClass := Some("lepus.server.LepusServer"),
    appProcessForkOptions     := {
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
    background := Def.inputTask {
      Actions.startBackground(
        projectRef = thisProjectRef.value,
        options    = forkOptions.value,
        mainClass  = (Compile / run / mainClass).value,
        classpath  = (Runtime / fullClasspath).value
      )
    }.dependsOn(Compile / products).evaluated,
    stop := Actions.stopApp(thisProjectRef.value),
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
