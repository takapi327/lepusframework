/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.sbt

import scala.sys.process.Process

import sbt.{ ProjectRef, Logger }

case class AppProcess(
  projectRef: ProjectRef,
  logger:     Logger
)(process: Process) {

  @volatile var finishState: Option[Int] = None

  val shutdownHook = shutdownHookThread

  def shutdownHookThread = new Thread(new Runnable {
    override def run(): Unit = if (isRunning) {
      logger.info("...killing process...")
      process.destroy()
    }
  })

  def stop: Int = {
    Runtime.getRuntime.removeShutdownHook(shutdownHook)
    process.destroy()
    process.exitValue()
  }

  def isRunning: Boolean = finishState.isEmpty
}
