/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.sbt

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec
import scala.sys.process.Process

import sbt.ProjectRef

/** Class for managing application processes with AtomicReference.
  *
  * @param processes
  *   Map with sbt project as key and application process as value.
  */
case class ProcessState(processes: Map[ProjectRef, Process]) {

  /** Update the application process corresponding to the sbt project.
    *
    * @param projectRef
    *   Class for uniquely referencing a project by URI and project identifier (String).
    * @param process
    *   Object for managing application processes.
    */
  def updateProcesses(projectRef: ProjectRef, process: Process): ProcessState =
    copy(processes + (projectRef -> process))

  /** Remove the application process corresponding to the sbt project.
    *
    * @param projectRef
    *   Class for uniquely referencing a project by URI and project identifier (String).
    */
  def removeProcess(projectRef: ProjectRef): ProcessState =
    processes.get(projectRef) match {
      case Some(_) => copy(processes - projectRef)
      case None    => this
    }

  /** Get the application process corresponding to the sbt project.
    *
    * @param projectRef
    *   Class for uniquely referencing a project by URI and project identifier (String).
    */
  def getProcess(projectRef: ProjectRef): Option[Process] =
    processes.get(projectRef)
}

object ProcessState {

  /** A method to initialize the application process with empty. */
  def initial: ProcessState = ProcessState(Map.empty)

  /** Variable for storing the application process in AtomicReference. */
  private val state = new AtomicReference(initial)

  /** Methods for updating from the old State to the new State. */
  @tailrec def update(action: ProcessState => ProcessState): ProcessState = {
    val oldState = state.get()
    val newState = action(oldState)
    if (!state.compareAndSet(oldState, newState)) update(action)
    else newState
  }

  /** Alias to get the value of State. */
  def get: ProcessState = state.get()
}
