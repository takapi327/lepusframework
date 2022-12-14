/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import cats.{ Eval, Monad }
import cats.syntax.all.*

import cats.effect.IO
import cats.effect.kernel.Clock
import cats.effect.std.Console

/** An object to enforce the minimum required values for log output.
  *
  * @tparam F
  *   the effect type.
  *
  * example: [[LoggingIO]]
  */
trait LoggingF[F[_]]:

  def output:    OutputF[F]
  def filter:    Filter
  def formatter: Formatter

  def logger: LoggerF[F]

trait LoggingIO extends LoggingF[IO]:

  override val output:    OutputF[IO] = ConsoleOutput[IO]
  override val filter:    Filter      = Filter.everything
  override val formatter: Formatter   = DefaultFormatter

  val logger: LoggerF[IO] = new LoggerF[IO]:

    private def buildLogMessage[M](
      level: Level,
      msg:   => M,
      ex:    Option[Throwable],
      ctx:   Map[String, String]
    ): ExecuteF[IO, LogMessage] =
      Clock[IO].realTime.map(now =>
        LogMessage(
          level,
          Eval.later(msg.toString),
          summon[ExecLocation],
          ctx,
          ex,
          Thread.currentThread().getName,
          now.toMillis
        )
      )

    private def doOutput(msg: LogMessage): ExecuteF[IO, Unit] =
      (msg.level, msg.exception) match
        case (Level.ERROR, Some(ex)) => output.outputError(formatter.format(msg)) >> output.outputStackTrace(ex)
        case (Level.ERROR, None)     => output.outputError(formatter.format(msg))
        case (_, Some(ex))           => output.output(formatter.format(msg)) >> output.outputStackTrace(ex)
        case _                       => output.output(formatter.format(msg))

    override protected def log[M](
      level: Level,
      msg:   => M,
      ex:    Option[Throwable],
      ctx:   Map[String, String]
    ): ExecuteF[IO, Unit] =
      buildLogMessage(level, msg, ex, ctx).flatMap(log)

    override protected def log(msg: LogMessage): ExecuteF[IO, Unit] =
      doOutput(msg).whenA(filter(msg))
