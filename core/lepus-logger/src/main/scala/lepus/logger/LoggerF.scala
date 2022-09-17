/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logger

import cats.~>

trait LoggerF[F[_]]:

  protected def log[M](level: Level, msg: => M, ex: Option[Throwable] = None, ctx: Map[String, String] = Map.empty): Execute[F, Unit]
  protected def log(msg: LogMessage): Execute[F, Unit]

  def trace[M](msg: => M): Execute[F, Unit] = log(Level.Trace, msg)
  def trace[M](msg: => M, ex: Throwable): Execute[F, Unit] = log(Level.Trace, msg, Some(ex))
  def trace[M](msg: => M, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Trace, msg, None, ctx)
  def trace[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Trace, msg, Some(ex), ctx)

  def debug[M](msg: => M): Execute[F, Unit] = log(Level.Debug, msg)
  def debug[M](msg: => M, ex: Throwable): Execute[F, Unit] = log(Level.Debug, msg, Some(ex))
  def debug[M](msg: => M, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Debug, msg, None, ctx)
  def debug[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Debug, msg, Some(ex), ctx)

  def info[M](msg: => M): Execute[F, Unit] = log(Level.Info, msg)
  def info[M](msg: => M, ex: Throwable): Execute[F, Unit] = log(Level.Info, msg, Some(ex))
  def info[M](msg: => M, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Info, msg, None, ctx)
  def info[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Info, msg, Some(ex), ctx)

  def warn[M](msg: => M): Execute[F, Unit] = log(Level.Warn, msg)
  def warn[M](msg: => M, ex: Throwable): Execute[F, Unit] = log(Level.Warn, msg, Some(ex))
  def warn[M](msg: => M, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Warn, msg, None, ctx)
  def warn[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Warn, msg, Some(ex), ctx)

  def error[M](msg: => M): Execute[F, Unit] = log(Level.Error, msg)
  def error[M](msg: => M, ex: Throwable): Execute[F, Unit] = log(Level.Error, msg, Some(ex))
  def error[M](msg: => M, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Error, msg, None, ctx)
  def error[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Error, msg, Some(ex), ctx)

object LoggerF:
  def apply[F[_]](using logger: LoggerF[F]): LoggerF[F] = logger

  extension [F[_]] (logger: LoggerF[F])
    def mapK[G[_]](f: F ~> G): LoggerF[G] = new LoggerF[G]:

      override protected def log[M](level: Level, msg: => M, ex: Option[Throwable], ctx: Map[String, String]): Execute[G, Unit] =
        f(logger.log(level, msg, ex, ctx))

      override protected def log(msg: LogMessage): Execute[G, Unit] =
        f(logger.log(msg))

      override def trace[M](msg: => M): Execute[G, Unit] =
        f(logger.trace(msg))

      override def trace[M](msg: => M, ex: Throwable): Execute[G, Unit] =
        f(logger.trace(msg, ex))

      override def trace[M](msg: => M, ctx: Map[String, String]): Execute[G, Unit] =
        f(logger.trace(msg, ctx))

      override def trace[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[G, Unit] =
        f(logger.trace(msg, ex, ctx))

      override def debug[M](msg: => M): Execute[G, Unit] =
        f(logger.debug(msg))

      override def debug[M](msg: => M, ex: Throwable): Execute[G, Unit] =
        f(logger.debug(msg, ex))

      override def debug[M](msg: => M, ctx: Map[String, String]): Execute[G, Unit] =
        f(logger.debug(msg, ctx))

      override def debug[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[G, Unit] =
        f(logger.debug(msg, ex, ctx))

      override def info[M](msg: => M): Execute[G, Unit] =
        f(logger.info(msg))

      override def info[M](msg: => M, ex: Throwable): Execute[G, Unit] =
        f(logger.info(msg, ex))

      override def info[M](msg: => M, ctx: Map[String, String]): Execute[G, Unit] =
        f(logger.info(msg, ctx))

      override def info[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[G, Unit] =
        f(logger.info(msg, ex, ctx))

      override def warn[M](msg: => M): Execute[G, Unit] =
        f(logger.warn(msg))

      override def warn[M](msg: => M, ex: Throwable): Execute[G, Unit] =
        f(logger.warn(msg, ex))

      override def warn[M](msg: => M, ctx: Map[String, String]): Execute[G, Unit] =
        f(logger.warn(msg, ctx))

      override def warn[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[G, Unit] =
        f(logger.warn(msg, ex, ctx))

      override def error[M](msg: => M): Execute[G, Unit] =
        f(logger.error(msg))

      override def error[M](msg: => M, ex: Throwable): Execute[G, Unit] =
        f(logger.error(msg, ex))

      override def error[M](msg: => M, ctx: Map[String, String]): Execute[G, Unit] =
        f(logger.error(msg, ctx))

      override def error[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[G, Unit] =
        f(logger.error(msg, ex, ctx))
