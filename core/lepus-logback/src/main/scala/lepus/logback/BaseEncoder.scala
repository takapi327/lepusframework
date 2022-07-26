/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logback

import java.time.{ LocalDateTime, Instant }
import java.util.TimeZone

import ch.qos.logback.core.LayoutBase
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.*

trait BaseEncoder extends LayoutBase[ILoggingEvent], ColorLog:

  protected def colorLevel(event: ILoggingEvent): String =
    event.getLevel match
      case Level.TRACE => white("TRACE")
      case Level.DEBUG => cyan("DEBUG")
      case Level.INFO  => blue("INFO")
      case Level.WARN  => yellow("WARN")
      case Level.ERROR => red("ERROR")

  protected def timeStampToLocalDateTime(timeStamp: Long): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), TimeZone.getDefault.toZoneId)
