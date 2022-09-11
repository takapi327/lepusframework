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

/** An object that extends woof's Logger. copied from woof:
  * https://github.com/LEGO/woof/blob/main/modules/core/shared/src/main/scala/org/legogroup/woof/Logger.scala
  *
  * @tparam F
  *   the effect type.
  */
trait Logger[F[_]: StringLocal: Monad: Clock](
  output:  Output[F],
  outputs: Output[F]*
)(using LepusPrinter, Filter)
  extends WoofLogger[F]:

  val stringLocal: StringLocal[F] = summon[StringLocal[F]]
  val printer:     LepusPrinter   = summon[LepusPrinter]
  val filter:      Filter         = summon[Filter]

  /** Methods for converting multiple arguments to strings for writing to the log.
    *
    * @param level
    *   Log level
    * @param info
    *   Information on the part of the log that was spit out
    * @param message
    *   log-displaying message
    * @param context
    *   IOLocal internal storage
    */
  private[lepus] def makeLogString(
    level:   LogLevel,
    info:    LogInfo,
    message: String,
    context: List[(String, String)]
  ): F[String] =
    Clock[F].realTime
      .map(d => EpochMillis(d.toMillis))
      .map(now => printer.toPrint(now, level, info, message, context))

  /** Methods for converting multiple arguments to strings for writing to the log.
    *
    * @param level
    *   Log level
    * @param info
    *   Information on the part of the log that was spit out
    * @param message
    *   log-displaying message
    * @param context
    *   IOLocal internal storage
    * @param exception
    *   exception information
    * @return
    */
  private[lepus] def makeLogString(
    level:     LogLevel,
    info:      LogInfo,
    message:   String,
    context:   List[(String, String)],
    exception: Throwable
  ): F[String] =
    Clock[F].realTime
      .map(d => EpochMillis(d.toMillis))
      .map(now => printer.toPrint(now, level, info, message, context, exception))

  /** Methods for processing to be written to the log.
    *
    * @param level
    *   Log level
    * @param s
    *   A string formatted for writing to the log.
    */
  private[lepus] def doOutputs(level: LogLevel, s: String): F[Unit] =
    val allOutputs = outputs.prepended(output)
    level match
      case LogLevel.Error => allOutputs.traverse_(_.outputError(s))
      case _              => allOutputs.traverse_(_.output(s))

  /** Methods for processing internal storage and formatting and writing logs.
    *
    * @param level
    *   Log level
    * @param message
    *   log-displaying message
    * @param logInfo
    *   Information on the part of the log that was spit out
    */
  override def doLog(level: LogLevel, message: String)(using logInfo: LogInfo): F[Unit] =
    for
      context <- stringLocal.ask
      logLine <- makeLogString(level, logInfo, message, context)
      _       <- doOutputs(level, logLine).whenA(filter(LogLine(level, logInfo, logLine, context)))
    yield ()

  /** Methods for processing internal storage and formatting and writing logs.
    *
    * @param level
    *   Log level
    * @param message
    *   log-displaying message
    * @param exception
    *   exception information
    * @param logInfo
    *   Information on the part of the log that was spit out
    */
  def doLog(level: LogLevel, message: String, exception: Throwable)(using logInfo: LogInfo): F[Unit] =
    for
      context <- stringLocal.ask
      logLine <- makeLogString(level, logInfo, message, context, exception)
      _       <- doOutputs(level, logLine).whenA(filter(LogLine(level, logInfo, logLine, context)))
    yield ()

  /** A method that allows you to specify the level of logging by the method name. */
  def debug(message: => String)(t: Throwable)(using LogInfo): F[Unit] = doLog(LogLevel.Debug, message, t)
  def error(message: => String)(t: Throwable)(using LogInfo): F[Unit] = doLog(LogLevel.Error, message, t)
  def info(message: => String)(t: Throwable)(using LogInfo):  F[Unit] = doLog(LogLevel.Info, message, t)
  def trace(message: => String)(t: Throwable)(using LogInfo): F[Unit] = doLog(LogLevel.Trace, message, t)
  def warn(message: => String)(t: Throwable)(using LogInfo):  F[Unit] = doLog(LogLevel.Warn, message, t)

object Logger:
  def apply[F[_]: StringLocal: Monad: Clock](
    output:  Output[F],
    outputs: Output[F]*
  )(using LepusPrinter, Filter): Logger[F] =
    new Logger[F](output, outputs: _*) {}
