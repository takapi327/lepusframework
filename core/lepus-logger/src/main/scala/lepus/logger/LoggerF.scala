/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logger

import cats.~>

trait LoggerF[F[_]]:

  protected def log[M](level: Level, msg: => M): F[Unit]
  protected def log[M](level: Level, msg: => M, ex: Throwable): F[Unit]
  protected def log[M](level: Level, msg: => M, ctx: Map[String, String]): F[Unit]
  protected def log[M](level: Level, msg: => M, ex: Throwable, ctx: Map[String, String]): F[Unit]
  protected def log(msg: LogMessage): F[Unit]

  def trace[M](msg: => M): F[Unit] = log(Level.Trace, msg)
  def trace[M](msg: => M, ex: Throwable): F[Unit] = log(Level.Trace, msg, ex)
  def trace[M](msg: => M, ctx: Map[String, String]): F[Unit] = log(Level.Trace, msg, ctx)
  def trace[M](msg: => M, ex: Throwable, ctx: Map[String, String]): F[Unit] = log(Level.Trace, msg, ex, ctx)

  def debug[M](msg: => M): F[Unit] = log(Level.Debug, msg)
  def debug[M](msg: => M, ex: Throwable): F[Unit] = log(Level.Debug, msg, ex)
  def debug[M](msg: => M, ctx: Map[String, String]): F[Unit] = log(Level.Debug, msg, ctx)
  def debug[M](msg: => M, ex: Throwable, ctx: Map[String, String]): F[Unit] = log(Level.Debug, msg, ex, ctx)

  def info[M](msg: => M): F[Unit] = log(Level.Info, msg)
  def info[M](msg: => M, ex: Throwable): F[Unit] = log(Level.Info, msg, ex)
  def info[M](msg: => M, ctx: Map[String, String]): F[Unit] = log(Level.Info, msg, ctx)
  def info[M](msg: => M, ex: Throwable, ctx: Map[String, String]): F[Unit] = log(Level.Info, msg, ex, ctx)

  def warn[M](msg: => M): F[Unit] = log(Level.Warn, msg)
  def warn[M](msg: => M, ex: Throwable): F[Unit] = log(Level.Warn, msg, ex)
  def warn[M](msg: => M, ctx: Map[String, String]): F[Unit] = log(Level.Warn, msg, ctx)
  def warn[M](msg: => M, ex: Throwable, ctx: Map[String, String]): F[Unit] = log(Level.Warn, msg, ex, ctx)

  def error[M](msg: => M): F[Unit] = log(Level.Error, msg)
  def error[M](msg: => M, ex: Throwable): F[Unit] = log(Level.Error, msg, ex)
  def error[M](msg: => M, ctx: Map[String, String]): F[Unit] = log(Level.Error, msg, ctx)
  def error[M](msg: => M, ex: Throwable, ctx: Map[String, String]): F[Unit] = log(Level.Error, msg, ex, ctx)

object LoggerF:
  def apply[F[_]](using logger: LoggerF[F]): LoggerF[F] = logger

  extension [F[_]] (logger: LoggerF[F])
    def mapK[G[_]](f: F ~> G): LoggerF[G] = new LoggerF[G]:
      override def log[M](level: Level, msg: => M): G[Unit] =
        f(logger.log(level, msg))

      override def log[M](level: Level, msg: => M, ex: Throwable): G[Unit] =
        f(logger.log(level, msg, ex))

      override def log[M](level: Level, msg: => M, ctx: Map[String, String]): G[Unit] =
        f(logger.log(level, msg, ctx))

      override def log[M](level: Level, msg: => M, ex: Throwable, ctx: Map[String, String]): G[Unit] =
        f(logger.log(level, msg, ex, ctx))

      override def log(msg: LogMessage): G[Unit] =
        f(logger.log(msg))

      override def trace[M](msg: => M): G[Unit] =
        f(logger.trace(msg))

      override def trace[M](msg: => M, ex: Throwable): G[Unit] =
        f(logger.trace(msg, ex))

      override def trace[M](msg: => M, ctx: Map[String, String]): G[Unit] =
        f(logger.trace(msg, ctx))

      override def trace[M](msg: => M, ex: Throwable, ctx: Map[String, String]): G[Unit] =
        f(logger.trace(msg, ex, ctx))

      override def debug[M](msg: => M): G[Unit] =
        f(logger.debug(msg))

      override def debug[M](msg: => M, ex: Throwable): G[Unit] =
        f(logger.debug(msg, ex))

      override def debug[M](msg: => M, ctx: Map[String, String]): G[Unit] =
        f(logger.debug(msg, ctx))

      override def debug[M](msg: => M, ex: Throwable, ctx: Map[String, String]): G[Unit] =
        f(logger.debug(msg, ex, ctx))

      override def info[M](msg: => M): G[Unit] =
        f(logger.info(msg))

      override def info[M](msg: => M, ex: Throwable): G[Unit] =
        f(logger.info(msg, ex))

      override def info[M](msg: => M, ctx: Map[String, String]): G[Unit] =
        f(logger.info(msg, ctx))

      override def info[M](msg: => M, ex: Throwable, ctx: Map[String, String]): G[Unit] =
        f(logger.info(msg, ex, ctx))

      override def warn[M](msg: => M): G[Unit] =
        f(logger.warn(msg))

      override def warn[M](msg: => M, ex: Throwable): G[Unit] =
        f(logger.warn(msg, ex))

      override def warn[M](msg: => M, ctx: Map[String, String]): G[Unit] =
        f(logger.warn(msg, ctx))

      override def warn[M](msg: => M, ex: Throwable, ctx: Map[String, String]): G[Unit] =
        f(logger.warn(msg, ex, ctx))

      override def error[M](msg: => M): G[Unit] =
        f(logger.error(msg))

      override def error[M](msg: => M, ex: Throwable): G[Unit] =
        f(logger.error(msg, ex))

      override def error[M](msg: => M, ctx: Map[String, String]): G[Unit] =
        f(logger.error(msg, ctx))

      override def error[M](msg: => M, ex: Throwable, ctx: Map[String, String]): G[Unit] =
        f(logger.error(msg, ex, ctx))
