/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.sbt

import sbt._
import sbt.Keys._

object LepusCommands {

  val baseClassloaderTask = Def.task {
    val classpath = (Compile / dependencyClasspath).value
    val log       = streams.value.log
    val parent    = ClassLoader.getSystemClassLoader.getParent
    log.debug("Using parent loader for base classloader: " + parent)

    new java.net.URLClassLoader(classpath.map(_.data.toURI.toURL).toArray, parent)
  }

  val swaggerCommand = Command.command("generateApi") { (state: State) =>
    val extracted = Project.extract(state)
    val settingUpdated: State = extracted.appendWithSession(Seq(Compile / run / mainClass := Some("lepus.sbt.LepusGenerator")), state)
    MainLoop.processCommand(Exec(s"run", None), settingUpdated)
  }
}
