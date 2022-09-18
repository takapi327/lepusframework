/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logger

/**
 * A base object with the necessary implementation for log output.
 *
 * @tparam F
 *   the effect type.
 */
trait LoggerF[F[_]]:

  /**
   * A method to receive all information and perform logging.
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
  protected def log[M](level: Level, msg: => M, ex: Option[Throwable] = None, ctx: Map[String, String] = Map.empty): Execute[F, Unit]

  /**
   * Methods for receiving LogMessage and executing logging.
   *
   * @param msg
   *   A class that summarizes the information needed to write out logs.
   */
  protected def log(msg: LogMessage): Execute[F, Unit]

  /** A set of alias methods to log trace levels. */
  inline def trace[M](msg: => M): Execute[F, Unit] = log(Level.Trace, msg)
  inline def trace[M](msg: => M, ex: Throwable): Execute[F, Unit] = log(Level.Trace, msg, Some(ex))
  inline def trace[M](msg: => M, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Trace, msg, None, ctx)
  inline def trace[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Trace, msg, Some(ex), ctx)

  /** A set of alias methods to log debug levels. */
  inline def debug[M](msg: => M): Execute[F, Unit] = log(Level.Debug, msg)
  inline def debug[M](msg: => M, ex: Throwable): Execute[F, Unit] = log(Level.Debug, msg, Some(ex))
  inline def debug[M](msg: => M, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Debug, msg, None, ctx)
  inline def debug[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Debug, msg, Some(ex), ctx)

  /** A set of alias methods to log info levels. */
  inline def info[M](msg: => M): Execute[F, Unit] = log(Level.Info, msg)
  inline def info[M](msg: => M, ex: Throwable): Execute[F, Unit] = log(Level.Info, msg, Some(ex))
  inline def info[M](msg: => M, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Info, msg, None, ctx)
  inline def info[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Info, msg, Some(ex), ctx)

  /** A set of alias methods to log warn levels. */
  inline def warn[M](msg: => M): Execute[F, Unit] = log(Level.Warn, msg)
  inline def warn[M](msg: => M, ex: Throwable): Execute[F, Unit] = log(Level.Warn, msg, Some(ex))
  inline def warn[M](msg: => M, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Warn, msg, None, ctx)
  inline def warn[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Warn, msg, Some(ex), ctx)

  /** A set of alias methods to log error levels. */
  inline def error[M](msg: => M): Execute[F, Unit] = log(Level.Error, msg)
  inline def error[M](msg: => M, ex: Throwable): Execute[F, Unit] = log(Level.Error, msg, Some(ex))
  inline def error[M](msg: => M, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Error, msg, None, ctx)
  inline def error[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[F, Unit] = log(Level.Error, msg, Some(ex), ctx)

object LoggerF:
  def apply[F[_]](using logger: LoggerF[F]): LoggerF[F] = logger
