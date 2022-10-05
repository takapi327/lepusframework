/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logger

import cats.~>

/** A base object with the necessary implementation for log output. */
trait Logger:

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
  ): Execute[Unit]

  /** Methods for receiving LogMessage and executing logging.
   *
   * @param msg
   *   A class that summarizes the information needed to write out logs.
   */
  protected def log(msg: LogMessage): Execute[Unit]

  /** A set of alias methods to log trace levels. */
  def trace[M](msg: => M):                           Execute[Unit] = log(Level.Trace, msg)
  def trace[M](msg: => M, ex: Throwable):            Execute[Unit] = log(Level.Trace, msg, Some(ex))
  def trace[M](msg: => M, ctx: Map[String, String]): Execute[Unit] = log(Level.Trace, msg, None, ctx)
  def trace[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[Unit] =
    log(Level.Trace, msg, Some(ex), ctx)

  /** A set of alias methods to log debug levels. */
  def debug[M](msg: => M):                           Execute[Unit] = log(Level.Debug, msg)
  def debug[M](msg: => M, ex: Throwable):            Execute[Unit] = log(Level.Debug, msg, Some(ex))
  def debug[M](msg: => M, ctx: Map[String, String]): Execute[Unit] = log(Level.Debug, msg, None, ctx)
  def debug[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[Unit] =
    log(Level.Debug, msg, Some(ex), ctx)

  /** A set of alias methods to log info levels. */
  def info[M](msg: => M):                           Execute[Unit] = log(Level.Info, msg)
  def info[M](msg: => M, ex: Throwable):            Execute[Unit] = log(Level.Info, msg, Some(ex))
  def info[M](msg: => M, ctx: Map[String, String]): Execute[Unit] = log(Level.Info, msg, None, ctx)
  def info[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[Unit] =
    log(Level.Info, msg, Some(ex), ctx)

  /** A set of alias methods to log warn levels. */
  def warn[M](msg: => M):                           Execute[Unit] = log(Level.Warn, msg)
  def warn[M](msg: => M, ex: Throwable):            Execute[Unit] = log(Level.Warn, msg, Some(ex))
  def warn[M](msg: => M, ctx: Map[String, String]): Execute[Unit] = log(Level.Warn, msg, None, ctx)
  def warn[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[Unit] =
    log(Level.Warn, msg, Some(ex), ctx)

  /** A set of alias methods to log error levels. */
  def error[M](msg: => M):                           Execute[Unit] = log(Level.Error, msg)
  def error[M](msg: => M, ex: Throwable):            Execute[Unit] = log(Level.Error, msg, Some(ex))
  def error[M](msg: => M, ctx: Map[String, String]): Execute[Unit] = log(Level.Error, msg, None, ctx)
  def error[M](msg: => M, ex: Throwable, ctx: Map[String, String]): Execute[Unit] =
    log(Level.Error, msg, Some(ex), ctx)

object Logger:
  def apply(using logger: Logger): Logger = logger
