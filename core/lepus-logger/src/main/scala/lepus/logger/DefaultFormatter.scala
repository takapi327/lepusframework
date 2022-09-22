/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import Color.*

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
    val message = withColor(Foreground.White, msg.message.value)
    s"$timestamp $level [$threadName] $enclosureName: $message ($fileName) $context"
