/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import org.legogroup.woof.{ ColorPrinter, EpochMillis, LogInfo, LogLevel, defaultTimeFormat }
import org.legogroup.woof.ColorPrinter.Theme

case class LepusPrinter(
  theme:      Theme                 = Theme.defaultTheme,
  formatTime: EpochMillis => String = defaultTimeFormat
) extends ColorPrinter(theme, formatTime):

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
          .map((key, value) => s"${ theme.contextKey }$key${ theme.reset }=${ theme.contextValue }$value")
          .mkString(", ") + theme.reset.getCode
    s"""
      |$time $levelColor[$prefix]$reset$contextPart $postfixColor${ info.prefix }$reset: $message $postfixColor${ info.postfix }$reset
      |${ exception.getMessage }
      |${ stackTrace(exception.getStackTrace) }
    """.stripMargin

  private def stackTrace(traces: Array[StackTraceElement]): String =
    traces
      .map { trace =>
        s"${ trace.getClassName }.${ trace.getMethodName } [${ trace.getFileName }:${ trace.getLineNumber }]"
      }
      .mkString("\n")
