/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import java.time.format.DateTimeFormatter
import java.time.{ Instant, ZoneId }

import scala.io.AnsiColor.RESET

import cats.Show
import cats.syntax.show.*

import Color.*

/**
 * An object for converting a [[LogMessage]] to a string in a specified format.
 *
 * example: [[DefaultFormatter]]
 * {{{
 *   object DefaultFormatter extends Formatter:
 *     override def format(msg: LogMessage): String =
 *       val timestamp = withColor(Foreground.White, Formatter.formatTimestamp(msg.timestamp))
 *       val level = msg.level match
 *         case Level.Trace => withColor(Foreground.Cyan, msg.level.toString)
 *         case Level.Debug => withColor(Foreground.White, msg.level.toString)
 *         case Level.Info  => withColor(Foreground.Blue, msg.level.toString)
 *         case Level.Warn  => withColor(Foreground.Yellow, msg.level.toString)
 *         case Level.Error => withColor(Foreground.Red, msg.level.toString)
 *       val context       = withColor(Foreground.White, Formatter.formatCtx(msg.context))
 *       val threadName    = withColor(Foreground.Green, msg.threadName)
 *       val enclosureName = withColor(Foreground.Magenta, msg.execLocation.enclosureName)
 *       val fileName = withColor(
 *         Foreground.Blue withStyle Style.Underlined,
 *         s"${ msg.execLocation.fileName }:${ msg.execLocation.lineNumber }"
 *       )
 *       val message = withColor(Foreground.White, msg.message)
 *       s"$timestamp $level [$threadName] $enclosureName: $message ($fileName) $context"
 * }}}
 */
trait Formatter:

  /**
   * Methods to enable message strings to be displayed in a specified color.
   *
   * @param color
   *   An object that summarizes [[scala.io.AnsiColor]] into an enum.
   * @param msg
   *   Log Message
   */
  inline def withColor[C <: Color](color: C, msg: String)(using Show[C]): String =
    show"$color$msg$RESET"

  /**
   * Methods for converting a [[LogMessage]] to a string in a specified format.
   *
   * @param msg
   *   Log Message
   */
  def format(msg: LogMessage): String

object Formatter:

  /**
   * Methods for converting Context information to string.
   *
   * @param context
   *   Context information to be included in the log
   */
  def formatCtx(context: Map[String, String]): String =
    if context.isEmpty then ""
    else
      context
        .map(ctx => {
          val (key, value) = ctx
          s"$key=$value"
        })
        .mkString(",")

  /**
   * A method to convert the logged execution time into a string.
   *
   * @param timestamp
   *   Execution time logged
   */
  def formatTimestamp(timestamp: Long): String =
    DateTimeFormatter
      .ofPattern("YYYY-MM-dd HH:mm:ss")
      .withZone(ZoneId.systemDefault())
      .format(Instant.ofEpochMilli(timestamp))

object DefaultFormatter extends Formatter:
  override def format(msg: LogMessage): String =
    val timestamp = withColor(Foreground.White, Formatter.formatTimestamp(msg.timestamp))
    val level = msg.level match
      case Level.Trace => withColor(Foreground.Cyan, msg.level.toString)
      case Level.Debug => withColor(Foreground.White, msg.level.toString)
      case Level.Info  => withColor(Foreground.Blue, msg.level.toString)
      case Level.Warn  => withColor(Foreground.Yellow, msg.level.toString)
      case Level.Error => withColor(Foreground.Red, msg.level.toString)
    val context       = withColor(Foreground.White, Formatter.formatCtx(msg.context))
    val threadName    = withColor(Foreground.Green, msg.threadName)
    val enclosureName = withColor(Foreground.Magenta, msg.execLocation.enclosureName)
    val fileName = withColor(
      Foreground.Blue withStyle Style.Underlined,
      s"${ msg.execLocation.fileName }:${ msg.execLocation.lineNumber }"
    )
    val message = withColor(Foreground.White, msg.message)
    s"$timestamp $level [$threadName] $enclosureName: $message ($fileName) $context"
