/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.sbt

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec

import sbt.ProjectRef

case class ProcessState(processes: Map[ProjectRef, AppProcess]) {
  def updateProcesses(projectRef: ProjectRef, process: AppProcess): ProcessState =
    copy(processes + (projectRef -> process))
  def removeProcess(projectRef: ProjectRef): ProcessState =
    processes.get(projectRef) match {
      case Some(_) => copy(processes - projectRef)
      case None    => this
    }
  def getProcess(projectRef: ProjectRef): Option[AppProcess] =
    processes.get(projectRef)
}

object ProcessState {
  def initial: ProcessState = ProcessState(Map.empty)

  private val state = new AtomicReference(initial)

  @tailrec def update(action: ProcessState => ProcessState): ProcessState = {
    val oldState = state.get()
    val newState = action(oldState)
    if (!state.compareAndSet(oldState, newState)) update(action)
    else newState
  }

  def get: ProcessState = state.get()
}
