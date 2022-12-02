/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import javax.sql.DataSource as JDataSource

import cats.Eval

import cats.effect.*
import cats.effect.implicits.*
import cats.effect.std.Console

import cats.implicits.*

import lepus.logger.{ *, given }

/** A model for building a database.
  *
  * @param dataSource
  *   Configuration of database settings to be retrieved from Conf file
  * @param sync$F$0
  *   A type class that encodes the notion of suspending synchronous side effects in the F[_] context
  * @param async$F$1
  *   A type class that encodes the notion of suspending asynchronous side effects in the F[_] context
  * @param console$F$2
  *   Effect type agnostic Console with common methods to write to and read from the standard console. Suited only for
  *   extremely simple console input and output.
  * @tparam F
  *   the effect type.
  */
private[lepus] trait DatabaseBuilder[F[_]: Sync: Async: Console, T <: JDataSource] extends LoggingF[F]:

  override val output:    OutputF[F] = ConsoleOutput[F]
  override val filter:    Filter     = Filter.everything
  override val formatter: Formatter  = DefaultFormatter

  val logger: LoggerF[F] = new LoggerF[F]:

    private def buildLogMessage[M](
      level: Level,
      msg:   => M,
      ex:    Option[Throwable],
      ctx:   Map[String, String]
    ): ExecuteF[F, LogMessage] =
      Clock[F].realTime.map(now =>
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

    private def doOutput(msg: LogMessage): ExecuteF[F, Unit] =
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
    ): ExecuteF[F, Unit] =
      buildLogMessage(level, msg, ex, ctx).flatMap(log)

    override protected def log(msg: LogMessage): ExecuteF[F, Unit] =
      doOutput(msg).whenA(filter(msg))

  def buildContext(): Resource[F, T]
