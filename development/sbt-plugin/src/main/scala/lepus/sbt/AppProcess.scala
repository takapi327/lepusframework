/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.sbt

import scala.sys.process.Process

import sbt.{ ProjectRef, Logger }

/** Class for managing application processes.
  *
  * @param projectRef
  *   Class for uniquely referencing a project by URI and project identifier (String).
  * @param logger
  *   Class for specifying the format of the logs to be spit out when the sbt project is executed.
  * @param process
  *   Represents a process that is running or has finished running.
  */
case class AppProcess(
  projectRef: ProjectRef,
  logger:     Logger
)(process:    Process) {

  @volatile var finishState: Option[Int] = None

  /** Stop the application process. */
  def stop: Int = {
    process.destroy()
    process.exitValue()
  }

  /** Value to check if the application process is running. */
  def isRunning: Boolean = finishState.isEmpty
}
