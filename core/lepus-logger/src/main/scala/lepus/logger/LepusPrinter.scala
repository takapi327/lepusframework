/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import org.legogroup.woof.{ ColorPrinter, EpochMillis, LogInfo, LogLevel, defaultTimeFormat }
import org.legogroup.woof.ColorPrinter.Theme

/** A model for writing out and processing logs.
  *
  * @param theme
  *   Format of the string to be written to the log
  * @param formatTime
  *   Format of log occurrence time
  */
case class LepusPrinter(
  theme:      Theme                 = Theme.defaultTheme,
  formatTime: EpochMillis => String = defaultTimeFormat
) extends ColorPrinter(theme, formatTime):

  /** Methods for receiving Throwables and writing them to the log.
    *
    * @param epochMillis
    *   Time when logging occurs
    * @param level
    *   Log level
    * @param info
    *   Information on the location where the log occurs
    * @param message
    *   Message to be written to the log
    * @param context
    *   Logging stored in IOLocal
    * @param exception
    *   Exception to be written to the log
    */
  def toPrint(
    epochMillis: EpochMillis,
    level:       LogLevel,
    info:        LogInfo,
    message:     String,
    context:     List[(String, String)],
    exception:   Throwable
  ): String =
    val levelColor   = theme.levelFormat(level)
    val postfixColor = theme.postfixFormat
    val reset        = theme.reset
    val prefix       = level.productPrefix.toUpperCase.padTo(5, ' ')
    val time         = formatTime(epochMillis)
    val contextPart =
      if context.isEmpty then ""
      else
        context
          .map { (key, value) =>
            s"${ theme.contextKey }$key${ theme.reset }=${ theme.contextValue }$value"
          }
          .mkString(", ") + theme.reset.getCode

    s"""
      |$time $levelColor[$prefix]$reset$contextPart $postfixColor${ info.prefix }$reset: $message $postfixColor${ info.postfix }$reset
      |${ exception.getMessage }
      |${ stackTrace(exception.getStackTrace) }
    """.stripMargin

  /** The process of converting the exception's stack trace into a string so that it can be written to the log.
    *
    * @param traces
    *   Array of stack traces that the exception has
    */
  private def stackTrace(traces: Array[StackTraceElement]): String =
    traces
      .map { trace =>
        s"${ trace.getClassName }.${ trace.getMethodName } [${ trace.getFileName }:${ trace.getLineNumber }]"
      }
      .mkString("\n")
