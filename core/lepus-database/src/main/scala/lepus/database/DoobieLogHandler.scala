/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.database

import scala.concurrent.duration.*

import doobie.util.log.{ ExecFailure, LogHandler, ProcessingFailure, Success }

import lepus.logger.{ ExecLocation, DefaultLogging }

trait DoobieLogHandler(using ExecLocation) extends DefaultLogging:

  private val slowThreshold = 200.millis

  protected val logHandler: LogHandler = LogHandler {
    case Success(sql, args, exec, processing) =>
      if exec > slowThreshold || processing > slowThreshold then
        logger.warn(
          s"""Slow query (execution: $exec, processing: $processing):
             |
             | ${sql.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
             |
             | arguments = [${args.mkString(", ")}]
             |   elapsed = ${exec.toMillis} ms exec + ${processing.toMillis} ms processing (${(exec + processing).toMillis} ms total)
             |""".stripMargin
        )
      else
        logger.info(
          s"""Successful Statement Execution:
             |
             | ${sql.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
             |
             | arguments = [${args.mkString(", ")}]
             |   elapsed = ${exec.toMillis} ms exec + ${processing.toMillis} ms processing (${(exec + processing).toMillis} ms total)
             |""".stripMargin
        )
    case ProcessingFailure(sql, args, exec, processing, failure) =>
      logger.error(
        s"""Failed Result Processing:
           |
           | ${sql.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
           |
           | arguments = [${args.mkString(", ")}]
           |   elapsed = ${exec.toMillis} ms exec + ${processing.toMillis} ms processing (failed) (${(exec + processing).toMillis} ms total)
           |   failure = ${failure.getMessage}
           |""".stripMargin,
        failure
      )
    case ExecFailure(sql, args, exec, failure) =>
      logger.error(
        s"""Failed Statement Execution:
           |
           | ${sql.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
           |
           | arguments = [${args.mkString(", ")}]
           |   elapsed = ${exec.toMillis} ms exec (failed)
           |   failure = ${failure.getMessage}
           |""".stripMargin,
        failure
      )
  }
