/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

/** A class that summarizes the information needed to write out logs.
  *
  * @param level
  *   Log Level
  * @param message
  *   Log Message
  * @param execLocation
  *   Information on the execution location that generated the log
  * @param context
  *   Context information to be included in the log
  * @param exception
  *   Exceptions to be included in the log
  * @param threadName
  *   Name of the thread that ran the log
  * @param timestamp
  *   Time the log was run
  */
case class LogMessage(
  level:        Level,
  message:      String,
  execLocation: ExecLocation,
  context:      Map[String, String],
  exception:    Option[Throwable],
  threadName:   String,
  timestamp:    Long
)
