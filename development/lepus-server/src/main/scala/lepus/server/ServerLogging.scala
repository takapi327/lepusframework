/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import cats.{ Eval, Monad }
import cats.syntax.all.*

import cats.effect.IO
import cats.effect.kernel.Clock
import cats.effect.std.Console

import org.typelevel.log4cats.Logger as Log4catsLogger

import lepus.logger.{ *, given }

trait ServerLogging[F[_]: Monad: Clock: Console] extends Logging[F]:
  override val output:    Output[F] = ConsoleOutput[F]
  override val filter:    Filter    = Filter.everything
  override val formatter: Formatter = DefaultFormatter

  val logger: LoggerF[F] = new LoggerF[F] with Log4catsLogger[F]:
    private def buildLogMessage[M](
      level: Level,
      msg:   => M,
      ex:    Option[Throwable],
      ctx:   Map[String, String]
    ): Execute[F, LogMessage] =
      Clock[F].realTime.map(now =>
        LogMessage(level, Eval.later(msg.toString), summon[ExecLocation], ctx, ex, Thread.currentThread().getName, now.toMillis)
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

    override def debug(message: => String): F[Unit] = log(Level.Debug, message)
    override def error(message: => String): F[Unit] = log(Level.Error, message)
    override def info(message: => String):  F[Unit] = log(Level.Info, message)
    override def trace(message: => String): F[Unit] = log(Level.Trace, message)
    override def warn(message: => String):  F[Unit] = log(Level.Warn, message)

    override def debug(t: Throwable)(message: => String): F[Unit] = debug(message, t)
    override def error(t: Throwable)(message: => String): F[Unit] = error(message, t)
    override def info(t: Throwable)(message: => String):  F[Unit] = info(message, t)
    override def trace(t: Throwable)(message: => String): F[Unit] = trace(message, t)
    override def warn(t: Throwable)(message: => String):  F[Unit] = warn(message, t)
