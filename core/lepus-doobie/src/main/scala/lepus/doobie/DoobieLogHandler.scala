/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie

import scala.concurrent.duration.*

import doobie.util.log.{ LogHandler, Success, ProcessingFailure, ExecFailure }

import lepus.logger.{ Logging, ExecLocation }

trait DoobieLogHandler[F[_]](using ExecLocation):
  self: Logging[F] =>

  private val slowThreshold = 200.millis

  given LogHandler = LogHandler {
    case Success(sql, args, exec, processing) =>
      if exec > slowThreshold || processing > slowThreshold then
        self.logger.warn(
          s"""Slow query (execution: $exec, processing: $processing):
             |
             | ${ sql.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ") }
             |
             | arguments = [${ args.mkString(", ") }]
             |   elapsed = ${ exec.toMillis } ms exec + ${ processing.toMillis } ms processing (${ (exec + processing).toMillis } ms total)
             |""".stripMargin
        )
      else
        self.logger.info(
          s"""Successful Statement Execution:
             |
             | ${ sql.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ") }
             |
             | arguments = [${ args.mkString(", ") }]
             |   elapsed = ${ exec.toMillis } ms exec + ${ processing.toMillis } ms processing (${ (exec + processing).toMillis } ms total)
             |""".stripMargin
        )
    case ProcessingFailure(sql, args, exec, processing, failure) =>
      self.logger.error(
        s"""Failed Result Processing:
           |
           | ${ sql.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ") }
           |
           | arguments = [${ args.mkString(", ") }]
           |   elapsed = ${ exec.toMillis } ms exec + ${ processing.toMillis } ms processing (failed) (${ (exec + processing).toMillis } ms total)
           |   failure = ${ failure.getMessage }
           |""".stripMargin,
        failure
      )
    case ExecFailure(sql, args, exec, failure) =>
      self.logger.error(
        s"""Failed Statement Execution:
           |
           | ${ sql.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ") }
           |
           | arguments = [${ args.mkString(", ") }]
           |   elapsed = ${ exec.toMillis } ms exec (failed)
           |   failure = ${ failure.getMessage }
           |""".stripMargin,
        failure
      )
  }
