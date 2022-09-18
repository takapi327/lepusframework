/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import cats.Monad
import cats.syntax.all.*

import cats.effect.kernel.Clock
import cats.effect.std.Console

/** An object to enforce the minimum required values for log output.
  *
  * @tparam F
  *   the effect type.
  *
  * example: [[LoggingIO]]
  */
trait Logging[F[_]]:

  def output:    Output[F]
  def filter:    Filter
  def formatter: Formatter

  def logger: LoggerF[F]

trait LoggingIO[F[_]: Monad: Clock: Console] extends Logging[F]:

  override val output:    Output[F] = ConsoleOutput[F]
  override val filter:    Filter    = Filter.everything
  override val formatter: Formatter = DefaultFormatter

  val logger: LoggerF[F] = new LoggerF[F]:

    private def buildLogMessage[M](
      level: Level,
      msg:   => M,
      ex:    Option[Throwable],
      ctx:   Map[String, String]
    ): Execute[F, LogMessage] =
      Clock[F].realTime.map(now =>
        LogMessage(level, msg.toString, summon[ExecLocation], ctx, ex, Thread.currentThread().getName, now.toMillis)
      )

    private def doOutput(msg: LogMessage): Execute[F, Unit] =
      (msg.level, msg.exception) match
        case (Level.Error, Some(ex)) => output.outputError(formatter.format(msg)) >> output.outputStackTrace(ex)
        case (Level.Error, None)     => output.outputError(formatter.format(msg))
        case (_, Some(ex))           => output.output(formatter.format(msg)) >> output.outputStackTrace(ex)
        case _                       => output.output(formatter.format(msg))

    override protected def log[M](
      level: Level,
      msg:   => M,
      ex:    Option[Throwable],
      ctx:   Map[String, String]
    ): Execute[F, Unit] =
      buildLogMessage(level, msg, ex, ctx).flatMap(log)

    override protected def log(msg: LogMessage): Execute[F, Unit] =
      doOutput(msg).whenA(filter(msg))
