/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logback

import java.util.UUID

import ch.qos.logback.core.CoreConstants
import ch.qos.logback.classic.spi.ILoggingEvent

/** Outputs system and application generated logs in a traceable format. Configure logback.groovy, logback-test.xml, or
 * logback.xml on the application classpath as follows
 *
 * For example:
 * {{{
 *   <configuration>
 *     <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
 *       <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
 *         <layout class="lepus.logback.LogbackTrackingEncoder" />
 *       </encoder>
 *     </appender>
 *
 *     <root level="INFO">
 *       <appender-ref ref="STDOUT" />
 *     </root>
 *   </<configuration>
 * }}}
 */
class LogbackTrackingEncoder extends BaseEncoder {
  override def doLayout(event: ILoggingEvent): String = {
    val sbuf: StringBuffer = new StringBuffer(128)
    sbuf.append(timeStampToLocalDateTime(event.getTimeStamp).toString)
    sbuf.append(" ")
    sbuf.append(colorLevel(event))
    sbuf.append(" [")
    sbuf.append(event.getThreadName)
    sbuf.append("] ")
    if (event.getMarker != null) {
      sbuf.append(" ")
      sbuf.append(event.getMarker)
      sbuf.append(" ")
    }
    sbuf.append(" [")
    sbuf.append(UUID.randomUUID.toString.replace("-", ""))
    sbuf.append("] ")
    sbuf.append(event.getLoggerName)
    sbuf.append(" - ")
    sbuf.append(event.getFormattedMessage)
    if (event.getThrowableProxy != null) {
      sbuf.append(" - ")
      sbuf.append(event.getThrowableProxy)
    }
    sbuf.append(CoreConstants.LINE_SEPARATOR)
    sbuf.toString
  }
}
