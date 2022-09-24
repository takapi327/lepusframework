/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.sbt

import scala.sys.process.Process

import sbt._
import sbt.Keys._

object LepusInternalKeys {

  val background = InputKey[Process]("background", "Starts the application in a forked JVM (in the background).")

  val stop = TaskKey[Unit]("stop", "Stops the application if it is currently running in the background")

  val appProcessForkOptions =
    TaskKey[ForkOptions]("app-process-fork-options", "The options needed for the start task for forking")

  val baseClassloader = TaskKey[ClassLoader](
    "baseClassloader",
    "The base classloader"
  )

  val externalizedResources = TaskKey[Seq[(File, String)]](
    label       = "externalizedResources",
    description = "The resources to externalize"
  )

  val lepusDependencyClasspath = TaskKey[Classpath](
    label       = "lepusDependencyClasspath",
    description = "The classpath containing all the jar dependencies of the project"
  )
}
