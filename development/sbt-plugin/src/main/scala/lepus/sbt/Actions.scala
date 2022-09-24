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

  /** The State of AtomicReference. */
  private def state = ProcessState.get

  /** Launch applications in the background. If the application is already running, stop it first.
    *
    * @param projectRef
    *   Class for uniquely referencing a project by URI and project identifier (String).
    * @param options
    *   Fork options for Java compilation.
    * @param mainClass
    *   Main class for starting the application.
    * @param classpath
    *   A path that includes the main class package.
    */
  def startBackground(
    projectRef: ProjectRef,
    options:    ForkOptions,
    mainClass:  Option[String],
    classpath:  Classpath
  ): Process = {
    stopApp(projectRef)
    startApp(projectRef, options, mainClass, classpath)
  }

  /** Start the application.
    *
    * @param projectRef
    *   Class for uniquely referencing a project by URI and project identifier (String).
    * @param options
    *   Fork options for Java compilation.
    * @param mainClass
    *   Main class for starting the application.
    * @param classpath
    *   A path that includes the main class package.
    */
  def startApp(
    projectRef: ProjectRef,
    options:    ForkOptions,
    mainClass:  Option[String],
    classpath:  Classpath
  ): Process = {
    val process = forkRun(options, mainClass.getOrElse(sys.error("No main class detected!")), classpath.map(_.data))
    registerState(projectRef, process)
    process
  }

  /** Stop the application.
    *
    * @param projectRef
    *   Class for uniquely referencing a project by URI and project identifier (String).
    */
  def stopApp(projectRef: ProjectRef): Unit = {
    state.getProcess(projectRef) match {
      case Some(process) =>
        logger.info("Stopping application %s (by killing the forked JVM) ..." format projectRef.project)
        process.stop
      case None => logger.info("Application %s not yet started" format projectRef.project)
    }
    removeState(projectRef)
  }

  /** The application process is stored in the State of AtomicReference.
    *
    * @param projectRef
    *   Class for uniquely referencing a project by URI and project identifier (String).
    * @param process
    *   Represents a process that is running or has finished running.
    */
  private[lepus] def registerState(projectRef: ProjectRef, process: Process): Unit =
    ProcessState.update { state =>
      val oldProcess = state.processes.get(projectRef)
      if (oldProcess.nonEmpty) oldProcess.get.stop
      state.updateProcesses(projectRef, process)
    }

  /** Remove the application process from the State of AtomicReference.
    *
    * @param projectRef
    *   Class for uniquely referencing a project by URI and project identifier (String).
    */
  private[lepus] def removeState(projectRef: ProjectRef): Unit =
    ProcessState.update { state =>
      state.removeProcess(projectRef)
    }

  /** Start the application process using sbt's Fork.
    *
    * @param options
    *   Fork options for Java compilation.
    * @param mainClass
    *   Main class for starting the application.
    * @param classpath
    *   A path that includes the main class package.
    */
  private[lepus] def forkRun(options: ForkOptions, mainClass: String, classpath: Seq[File]): Process = {
    logger.info("")
    logger.info("      Lepus Server started. To stop the server, execute the stop command.")
    logger.info("")
    logger.info("---------------------------------------------------------------------------------------")
    logger.info("----- To start the server with auto-reload enabled, run the 〜background command. -----")
    logger.info("---------------------------------------------------------------------------------------")
    logger.info("")

    val scalaOptions = Seq("-classpath", Path.makeString(classpath), mainClass)
    val newOptions   = options.withOutputStrategy(options.outputStrategy getOrElse LoggedOutput(logger))

    Fork.java.fork(newOptions, scalaOptions)
  }

  /**
   * Extension method of Process.
   *
   * @param process
   *   Object for managing application processes.
   */
  implicit class ProcessOpt(process: Process) {
    def stop: Int = {
      process.destroy()
      process.exitValue()
    }
  }
}
