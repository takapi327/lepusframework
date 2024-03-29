/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import cats.~>

/** A base object with the necessary implementation for log output.
  *
  * @tparam F
  *   the effect type.
  */
trait LoggerF[F[_]]:

  /** A method to receive all information and perform logging.
    *
    * @param level
    *   Log Level
    * @param msg
    *   Log Message
    * @param ex
    *   Exceptions to be included in the log
    * @param ctx
    *   Context information to be included in the log
    * @tparam M
    *   Types that can be converted to String
    */
  protected def log[M](
    level: Level,
    msg:   => M,
    ex:    Option[Throwable] = None,
    ctx:   Map[String, String] = Map.empty
  ): ExecuteF[F, Unit]

  /** Methods for receiving LogMessage and executing logging.
    *
    * @param msg
    *   A class that summarizes the information needed to write out logs.
    */
  protected def log(msg: LogMessage): ExecuteF[F, Unit]

  /** A set of alias methods to log trace levels. */
  def trace[M](msg: => M):                           ExecuteF[F, Unit] = log(Level.TRACE, msg)
  def trace[M](msg: => M, ex: Throwable):            ExecuteF[F, Unit] = log(Level.TRACE, msg, Some(ex))
  def trace[M](msg: => M, ctx: Map[String, String]): ExecuteF[F, Unit] = log(Level.TRACE, msg, None, ctx)
  def trace[M](msg: => M, ex: Throwable, ctx: Map[String, String]): ExecuteF[F, Unit] =
    log(Level.TRACE, msg, Some(ex), ctx)

  /** A set of alias methods to log debug levels. */
  def debug[M](msg: => M):                           ExecuteF[F, Unit] = log(Level.DEBUG, msg)
  def debug[M](msg: => M, ex: Throwable):            ExecuteF[F, Unit] = log(Level.DEBUG, msg, Some(ex))
  def debug[M](msg: => M, ctx: Map[String, String]): ExecuteF[F, Unit] = log(Level.DEBUG, msg, None, ctx)
  def debug[M](msg: => M, ex: Throwable, ctx: Map[String, String]): ExecuteF[F, Unit] =
    log(Level.DEBUG, msg, Some(ex), ctx)

  /** A set of alias methods to log info levels. */
  def info[M](msg: => M):                           ExecuteF[F, Unit] = log(Level.INFO, msg)
  def info[M](msg: => M, ex: Throwable):            ExecuteF[F, Unit] = log(Level.INFO, msg, Some(ex))
  def info[M](msg: => M, ctx: Map[String, String]): ExecuteF[F, Unit] = log(Level.INFO, msg, None, ctx)
  def info[M](msg: => M, ex: Throwable, ctx: Map[String, String]): ExecuteF[F, Unit] =
    log(Level.INFO, msg, Some(ex), ctx)

  /** A set of alias methods to log warn levels. */
  def warn[M](msg: => M):                           ExecuteF[F, Unit] = log(Level.WARN, msg)
  def warn[M](msg: => M, ex: Throwable):            ExecuteF[F, Unit] = log(Level.WARN, msg, Some(ex))
  def warn[M](msg: => M, ctx: Map[String, String]): ExecuteF[F, Unit] = log(Level.WARN, msg, None, ctx)
  def warn[M](msg: => M, ex: Throwable, ctx: Map[String, String]): ExecuteF[F, Unit] =
    log(Level.WARN, msg, Some(ex), ctx)

  /** A set of alias methods to log error levels. */
  def error[M](msg: => M):                           ExecuteF[F, Unit] = log(Level.ERROR, msg)
  def error[M](msg: => M, ex: Throwable):            ExecuteF[F, Unit] = log(Level.ERROR, msg, Some(ex))
  def error[M](msg: => M, ctx: Map[String, String]): ExecuteF[F, Unit] = log(Level.ERROR, msg, None, ctx)
  def error[M](msg: => M, ex: Throwable, ctx: Map[String, String]): ExecuteF[F, Unit] =
    log(Level.ERROR, msg, Some(ex), ctx)

object LoggerF:
  def apply[F[_]](using logger: LoggerF[F]): LoggerF[F] = logger

  extension [F[_]](logger: LoggerF[F])
    def mapK[G[_]](f: F ~> G): LoggerF[G] = new LoggerF[G]:

      override protected def log[M](
        level: Level,
        msg:   => M,
        ex:    Option[Throwable],
        ctx:   Map[String, String]
      ): ExecuteF[G, Unit] =
        f(logger.log(level, msg, ex, ctx))

      override protected def log(msg: LogMessage): ExecuteF[G, Unit] =
        f(logger.log(msg))

      override def trace[M](msg: => M): ExecuteF[G, Unit] =
        f(logger.trace(msg))

      override def trace[M](msg: => M, ex: Throwable): ExecuteF[G, Unit] =
        f(logger.trace(msg, ex))

      override def trace[M](msg: => M, ctx: Map[String, String]): ExecuteF[G, Unit] =
        f(logger.trace(msg, ctx))

      override def trace[M](msg: => M, ex: Throwable, ctx: Map[String, String]): ExecuteF[G, Unit] =
        f(logger.trace(msg, ex, ctx))

      override def debug[M](msg: => M): ExecuteF[G, Unit] =
        f(logger.debug(msg))

      override def debug[M](msg: => M, ex: Throwable): ExecuteF[G, Unit] =
        f(logger.debug(msg, ex))

      override def debug[M](msg: => M, ctx: Map[String, String]): ExecuteF[G, Unit] =
        f(logger.debug(msg, ctx))

      override def debug[M](msg: => M, ex: Throwable, ctx: Map[String, String]): ExecuteF[G, Unit] =
        f(logger.debug(msg, ex, ctx))

      override def info[M](msg: => M): ExecuteF[G, Unit] =
        f(logger.info(msg))

      override def info[M](msg: => M, ex: Throwable): ExecuteF[G, Unit] =
        f(logger.info(msg, ex))

      override def info[M](msg: => M, ctx: Map[String, String]): ExecuteF[G, Unit] =
        f(logger.info(msg, ctx))

      override def info[M](msg: => M, ex: Throwable, ctx: Map[String, String]): ExecuteF[G, Unit] =
        f(logger.info(msg, ex, ctx))

      override def warn[M](msg: => M): ExecuteF[G, Unit] =
        f(logger.warn(msg))

      override def warn[M](msg: => M, ex: Throwable): ExecuteF[G, Unit] =
        f(logger.warn(msg, ex))

      override def warn[M](msg: => M, ctx: Map[String, String]): ExecuteF[G, Unit] =
        f(logger.warn(msg, ctx))

      override def warn[M](msg: => M, ex: Throwable, ctx: Map[String, String]): ExecuteF[G, Unit] =
        f(logger.warn(msg, ex, ctx))

      override def error[M](msg: => M): ExecuteF[G, Unit] =
        f(logger.error(msg))

      override def error[M](msg: => M, ex: Throwable): ExecuteF[G, Unit] =
        f(logger.error(msg, ex))

      override def error[M](msg: => M, ctx: Map[String, String]): ExecuteF[G, Unit] =
        f(logger.error(msg, ctx))

      override def error[M](msg: => M, ex: Throwable, ctx: Map[String, String]): ExecuteF[G, Unit] =
        f(logger.error(msg, ex, ctx))
