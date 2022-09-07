/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import cats.Monad
import cats.syntax.all.*

import cats.effect.kernel.Clock

import org.legogroup.woof.*
import org.legogroup.woof.Logger as WoofLogger
import org.legogroup.woof.Logger.StringLocal

// copied from woof:
// https://github.com/LEGO/woof/blob/main/modules/core/shared/src/main/scala/org/legogroup/woof/Logger.scala
trait Logger[F[_]: StringLocal: Monad: Clock](
  output:  Output[F],
  outputs: Output[F]*
)(using LepusPrinter, Filter)
  extends WoofLogger[F]:

  val stringLocal: StringLocal[F] = summon[StringLocal[F]]
  val printer:     LepusPrinter   = summon[LepusPrinter]
  val filter:      Filter         = summon[Filter]

  private[lepus] def makeLogString(
    level:   LogLevel,
    info:    LogInfo,
    message: String,
    context: List[(String, String)]
  ): F[String] =
    Clock[F].realTime
      .map(d => EpochMillis(d.toMillis))
      .map(now => summon[LepusPrinter].toPrint(now, level, info, message, context))

  private[lepus] def makeLogString(
    level:     LogLevel,
    info:      LogInfo,
    message:   String,
    context:   List[(String, String)],
    exception: Throwable
  ): F[String] =
    Clock[F].realTime
      .map(d => EpochMillis(d.toMillis))
      .map(now => summon[LepusPrinter].toPrint(now, level, info, message, context, exception))

  private[lepus] def doOutputs(level: LogLevel, s: String): F[Unit] =
    val allOutputs = outputs.prepended(output)
    level match
      case LogLevel.Error => allOutputs.traverse_(_.outputError(s))
      case _              => allOutputs.traverse_(_.output(s))

  override def doLog(level: LogLevel, message: String)(using logInfo: LogInfo): F[Unit] =
    for
      context <- summon[StringLocal[F]].ask
      logLine <- makeLogString(level, logInfo, message, context)
      _       <- doOutputs(level, logLine).whenA(summon[Filter](LogLine(level, logInfo, logLine, context)))
    yield ()

  def doLog(level: LogLevel, message: String, exception: Throwable)(using logInfo: LogInfo): F[Unit] =
    for
      context <- summon[StringLocal[F]].ask
      logLine <- makeLogString(level, logInfo, message, context, exception)
      _       <- doOutputs(level, logLine).whenA(summon[Filter](LogLine(level, logInfo, logLine, context)))
    yield ()

  def debug(t: Throwable)(message: => String)(using LogInfo): F[Unit] = doLog(LogLevel.Debug, message, t)
  def error(t: Throwable)(message: => String)(using LogInfo): F[Unit] = doLog(LogLevel.Error, message, t)
  def info(t: Throwable)(message: => String)(using LogInfo):  F[Unit] = doLog(LogLevel.Info, message, t)
  def trace(t: Throwable)(message: => String)(using LogInfo): F[Unit] = doLog(LogLevel.Trace, message, t)
  def warn(t: Throwable)(message: => String)(using LogInfo):  F[Unit] = doLog(LogLevel.Warn, message, t)

object Logger:
  def apply[F[_]: StringLocal: Monad: Clock](
    output:  Output[F],
    outputs: Output[F]*
  )(using LepusPrinter, Filter): Logger[F] =
    new Logger[F](output, outputs: _*) {}
