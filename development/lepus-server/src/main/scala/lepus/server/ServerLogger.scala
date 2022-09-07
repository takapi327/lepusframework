/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import cats.Monad

import cats.effect.IO
import cats.effect.kernel.Clock

import org.legogroup.woof.{ Filter, Output, LogInfo, LogLevel }
import org.legogroup.woof.Logger.StringLocal
import org.legogroup.woof.local.Local

import org.typelevel.log4cats.Logger as Log4catsLogger

import lepus.logger.{ Logger, LepusPrinter }

class ServerLogger[F[_]: StringLocal: Monad: Clock](
  output:  Output[F],
  outputs: Output[F]*
)(using LepusPrinter, Filter, LogInfo)
  extends Logger[F](output, outputs: _*),
          Log4catsLogger[F]:

  override def debug(message: => String): F[Unit] = doLog(LogLevel.Debug, message)
  override def error(message: => String): F[Unit] = doLog(LogLevel.Error, message)
  override def info(message: => String):  F[Unit] = doLog(LogLevel.Info, message)
  override def trace(message: => String): F[Unit] = doLog(LogLevel.Trace, message)
  override def warn(message: => String):  F[Unit] = doLog(LogLevel.Warn, message)

  override def debug(t: Throwable)(message: => String): F[Unit] = doLog(LogLevel.Debug, message, t)
  override def error(t: Throwable)(message: => String): F[Unit] = doLog(LogLevel.Error, message, t)
  override def info(t: Throwable)(message: => String):  F[Unit] = doLog(LogLevel.Info, message, t)
  override def trace(t: Throwable)(message: => String): F[Unit] = doLog(LogLevel.Trace, message, t)
  override def warn(t: Throwable)(message: => String):  F[Unit] = doLog(LogLevel.Warn, message, t)

object ServerLogger:
  def apply[F[_]: StringLocal: Monad: Clock](
    output:  Output[F],
    outputs: Output[F]*
  )(using LepusPrinter, Filter, LogInfo): ServerLogger[F] =
    new ServerLogger[F](output, outputs: _*)
