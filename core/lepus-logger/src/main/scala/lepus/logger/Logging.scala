/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logger

import cats.{ Eval, Monad }
import cats.syntax.all.*

/** An object to enforce the minimum required values for log output. */
trait Logging:

  def output:    Output
  def filter:    Filter
  def formatter: Formatter

  def logger: Logger

trait DefaultLogging extends Logging:

  override val output:    Output    = new SystemOutput
  override val filter:    Filter    = Filter.everything
  override val formatter: Formatter = DefaultFormatter

  val logger: Logger = new Logger:

    private def buildLogMessage[M](
      level: Level,
      msg:   => M,
      ex:    Option[Throwable],
      ctx:   Map[String, String]
    ): Execute[LogMessage] =
      LogMessage(
        level,
        Eval.later(msg.toString),
        summon[ExecLocation],
        ctx,
        ex,
        Thread.currentThread().getName,
        System.currentTimeMillis()
      )

    private def doOutput(msg: LogMessage): Execute[Unit] =
      (msg.level, msg.exception) match
        case (Level.Error, Some(ex)) =>
          output.outputError(formatter.format(msg))
          output.outputStackTrace(ex)
        case (Level.Error, None)     => output.outputError(formatter.format(msg))
        case (_, Some(ex))           =>
          output.output(formatter.format(msg))
          output.outputStackTrace(ex)
        case _                       => output.output(formatter.format(msg))

    override protected def log[M](
      level: Level,
      msg:   => M,
      ex:    Option[Throwable],
      ctx:   Map[String, String]
    ): Execute[Unit] = log(buildLogMessage(level, msg, ex, ctx))

    /** Methods for receiving LogMessage and executing logging.
     *
     * @param msg
     * A class that summarizes the information needed to write out logs.
     */
    override protected def log(msg: LogMessage): Execute[Unit] =
      if filter(msg) then doOutput(msg) else ()
