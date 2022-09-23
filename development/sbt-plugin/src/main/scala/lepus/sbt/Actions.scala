/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.sbt

import java.io.File

import scala.sys.process.Process

import sbt.Keys._
import sbt.{ Fork, ForkOptions, LoggedOutput, Path, ProjectRef }

object Actions {

  val logger = ProcessLogger()

  private def state = ProcessState.get

  def startBackground(
    projectRef: ProjectRef,
    options:    ForkOptions,
    mainClass:  Option[String],
    classpath:  Classpath
  ): AppProcess = {
    stopApp(projectRef)
    startApp(projectRef, options, mainClass, classpath)
  }

  def startApp(
    projectRef: ProjectRef,
    options:    ForkOptions,
    mainClass:  Option[String],
    classpath:  Classpath
  ): AppProcess = {
    assert(!state.getProcess(projectRef).exists(_.isRunning))
    val appProcess = AppProcess(projectRef, logger) {
      forkRun(options, mainClass.getOrElse(sys.error("No main class detected!")), classpath.map(_.data))
    }
    registerState(projectRef, appProcess)
    appProcess
  }

  def stopApp(projectRef: ProjectRef): Unit = {
    state.getProcess(projectRef) match {
      case Some(process) => if (process.isRunning) {
        logger.info("Stopping application %s (by killing the forked JVM) ..." format process.projectRef.project)
        process.stop
      }
      case None => logger.info("Application %s not yet started" format projectRef.project)
    }
    removeState(projectRef)
  }

  private def registerState(projectRef: ProjectRef, process: AppProcess): Unit =
    ProcessState.update { state =>
      val oldProcess = state.processes.get(projectRef)
      if (oldProcess.exists(_.isRunning)) oldProcess.get.stop
      state.updateProcesses(projectRef, process)
    }

  private def removeState(projectRef: ProjectRef): Unit =
    ProcessState.update { state =>
      state.removeProcess(projectRef)
    }

  private def forkRun(options: ForkOptions, mainClass: String, classpath: Seq[File]): Process = {
    logger.info("")
    logger.info("      Lepus Server started. To stop the server, execute the stop command.")
    logger.info("")
    logger.info("---------------------------------------------------------------------------------------")
    logger.info("----- To start the server with auto-reload enabled, run the 〜background command. -----")
    logger.info("---------------------------------------------------------------------------------------")
    logger.info("")

    val scalaOptions = Seq("-classpath", Path.makeString(classpath), mainClass)
    val newOptions = options.withOutputStrategy(options.outputStrategy getOrElse LoggedOutput(logger))

    Fork.java.fork(newOptions, scalaOptions)
  }
}
