/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import java.sql.Timestamp
import java.time.LocalDateTime

import cats.Eval

import org.specs2.mutable.Specification

object FormatterTest extends Specification:
  "Testing the Formatter" should {
    "The context formatting process becomes the specified string." in {
      Formatter.formatCtx(Map("test" -> "hoge")) === "test=hoge"
    }

    "The context formatting process becomes the specified string." in {
      Formatter.formatCtx(Map("test" -> "hoge", "huga" -> "test")) === "test=hoge,huga=test"
    }

    "The context formatting process becomes the specified string." in {
      Formatter.formatCtx(Map.empty) === ""
    }

    "Context formatting process does not result in the specified string." in {
      Formatter.formatCtx(Map("test" -> "hoge")) !== ""
    }

    "Context formatting process does not result in the specified string." in {
      Formatter.formatCtx(Map.empty) !== "test=hoge,huga=test"
    }

    "Timestamp formatting process becomes the specified string." in {
      val timestamp = Timestamp.valueOf(LocalDateTime.of(2022, 9, 17, 20, 17, 42))
      Formatter.formatTimestamp(timestamp.getTime) === "2022-09-17 20:17:42"
    }

    "Timestamp formatting process does not result in the specified string." in {
      val timestamp = Timestamp.valueOf(LocalDateTime.now())
      Formatter.formatTimestamp(timestamp.getTime) !== "2022-09-17 20:17:42"
    }

    "The formatting process of DefaultFormatter becomes the specified string." in {
      val timestamp  = Timestamp.valueOf(LocalDateTime.of(2022, 9, 17, 20, 17, 42))
      val threadName = Thread.currentThread().getName
      val logMessage =
        LogMessage(Level.Info, Eval.later("test"), summon[ExecLocation], Map.empty, None, threadName, timestamp.getTime)
      val timestampStr     = DefaultFormatter.withColor(Color.Foreground.White, "2022-09-17 20:17:42")
      val levelStr         = DefaultFormatter.withColor(Color.Foreground.Blue, "Info")
      val threadNameStr    = DefaultFormatter.withColor(Color.Foreground.Green, threadName)
      val enclosureNameStr = DefaultFormatter.withColor(Color.Foreground.Magenta, "lepus.logger.FormatterTest$")
      val messageStr       = DefaultFormatter.withColor(Color.Foreground.White, "test")
      val fileNameStr =
        DefaultFormatter.withColor(Color.Foreground.Blue withStyle Color.Style.Underlined, "FormatterTest.scala:49")
      val contextStr = DefaultFormatter.withColor(Color.Foreground.White, "")
      DefaultFormatter.format(
        logMessage
      ) === s"$timestampStr $levelStr [$threadNameStr] $enclosureNameStr: $messageStr ($fileNameStr) $contextStr"
    }

    "DefaultFormatter formatting process does not result in the specified string." in {
      val timestamp  = Timestamp.valueOf(LocalDateTime.of(2022, 9, 17, 20, 17, 42))
      val threadName = Thread.currentThread().getName
      val logMessage =
        LogMessage(Level.Info, Eval.later("test"), summon[ExecLocation], Map.empty, None, threadName, timestamp.getTime)
      DefaultFormatter.format(
        logMessage
      ) !== s"2022-09-17 20:17:42 Info lepus.logger.FormatterTest$$: test (FormatterTest.scala:49)"
    }
  }
