/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logback

import ch.qos.logback.core.LayoutBase
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.classic.spi.ILoggingEvent

import io.circe.Json
import io.circe.syntax._

/** Outputs logs generated by the system and applications in Json format. Configure logback.groovy, logback-test.xml, or
  * logback.xml on the application classpath as follows
  *
  * For example:
  * {{{
  *   <configuration>
  *     <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
  *       <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
  *         <layout class="lepus.logback.LogbackJsonEncoder" />
  *       </encoder>
  *     </appender>
  *
  *     <root level="INFO">
  *       <appender-ref ref="STDOUT" />
  *     </root>
  *   </<configuration>
  * }}}
  */
object LogbackJsonEncoder extends LayoutBase[ILoggingEvent] {
  override def doLayout(event: ILoggingEvent): String =
    Json
      .obj(
        "timeStamp"        -> event.getTimeStamp.asJson,
        "level"            -> event.getLevel.toString.asJson,
        "maker"            -> event.getMarker.toString.asJson,
        "thread"           -> event.getThreadName.asJson,
        "message"          -> event.getMessage.asJson,
        "throwable"        -> event.getThrowableProxy.toString.asJson,
        "class"            -> event.getClass.toString.asJson,
        "argument"         -> event.getArgumentArray.mkString("Array(", ", ", ")").asJson,
        "callerData"       -> event.getCallerData.mkString("Array(", ", ", ")").asJson,
        "loggerContextVO"  -> event.getLoggerContextVO.toString.asJson,
        "formattedMessage" -> event.getFormattedMessage.asJson,
        "mdcPropertyMap"   -> event.getMDCPropertyMap.toString.asJson,
        "loggerName"       -> event.getLoggerName.asJson,
        "coreConstants"    -> CoreConstants.LINE_SEPARATOR.asJson
      )
      .toString()
}
