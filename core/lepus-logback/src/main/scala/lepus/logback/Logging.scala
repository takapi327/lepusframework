/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logback

import java.util.UUID

import org.slf4j.{ LoggerFactory, MarkerFactory, Logger as Slf4jLogger }
import org.slf4j.spi.LocationAwareLogger
import LocationAwareLogger.*

/** Used to write logs to specific system or application components. Inherit from the class or object to which the log
  * is to be planted.
  *
  * For example:
  * {{{
  *   object LoggingTest extends Logging:
  *     logger.info("Test log writing")
  * }}}
  */
trait Logging:
  protected[this] lazy val logger: Logger =
    val clazz = getClass.getName
    val name  = if clazz endsWith "$" then clazz.substring(0, clazz.length - 1) else clazz
    Logger(LoggerFactory.getLogger(name))

  protected[this] lazy val trackingLogger: TrackingLogger =
    val clazz = getClass.getName
    val name  = if clazz endsWith "$" then clazz.substring(0, clazz.length - 1) else clazz
    TrackingLogger(LoggerFactory.getLogger(name).asInstanceOf[LocationAwareLogger], name)

/** The TrackingLogger object is used to record messages and track logs for a particular system or application
  * component.
  * @param slf4jLogger
  *   Logger of slf4j to write logs
  */
private[lepus] sealed class Logger(val slf4jLogger: Slf4jLogger):

  inline def traceEnabled: Boolean = slf4jLogger.isTraceEnabled
  inline def debugEnabled: Boolean = slf4jLogger.isDebugEnabled
  inline def infoEnabled:  Boolean = slf4jLogger.isInfoEnabled
  inline def warnEnabled:  Boolean = slf4jLogger.isWarnEnabled
  inline def errorEnabled: Boolean = slf4jLogger.isErrorEnabled

  /** Message only log
    */
  inline def error(msg: => String): Unit = inline if errorEnabled then slf4jLogger.error(msg)
  inline def warn(msg: => String):  Unit = inline if warnEnabled then slf4jLogger.warn(msg)
  inline def info(msg: => String):  Unit = inline if infoEnabled then slf4jLogger.info(msg)
  inline def debug(msg: => String): Unit = inline if debugEnabled then slf4jLogger.debug(msg)
  inline def trace(msg: => String): Unit = inline if traceEnabled then slf4jLogger.trace(msg)

  /** Handle messages and exceptions
    */
  inline def error(msg: => String, throwable: Throwable): Unit =
    inline if errorEnabled then slf4jLogger.error(msg, throwable)
  inline def warn(msg: => String, throwable: Throwable): Unit =
    inline if warnEnabled then slf4jLogger.warn(msg, throwable)
  inline def info(msg: => String, throwable: Throwable): Unit =
    inline if infoEnabled then slf4jLogger.info(msg, throwable)
  inline def debug(msg: => String, throwable: Throwable): Unit =
    inline if debugEnabled then slf4jLogger.debug(msg, throwable)
  inline def trace(msg: => String, throwable: Throwable): Unit =
    inline if traceEnabled then slf4jLogger.trace(msg, throwable)

  /** Handle messages and Array Object
    */
  inline def error(msg: => String, argArray: Array[Object]): Unit =
    inline if errorEnabled then slf4jLogger.error(msg, argArray)
  inline def warn(msg: => String, argArray: Array[Object]): Unit =
    inline if warnEnabled then slf4jLogger.warn(msg, argArray)
  inline def info(msg: => String, argArray: Array[Object]): Unit =
    inline if infoEnabled then slf4jLogger.info(msg, argArray)
  inline def debug(msg: => String, argArray: Array[Object]): Unit =
    inline if debugEnabled then slf4jLogger.debug(msg, argArray)
  inline def trace(msg: => String, argArray: Array[Object]): Unit =
    inline if traceEnabled then slf4jLogger.trace(msg, argArray)

/** The TrackingLogger object is used to record messages and track logs for a particular system or application
  * component.
  *
  * @param logger
  *   LocationAwareLogger of slf4j api to write logs
  * @param className
  *   Name of the class calling the log
  */
private[lepus] sealed class TrackingLogger(logger: LocationAwareLogger, className: String):

  inline def traceEnabled: Boolean = logger.isTraceEnabled
  inline def debugEnabled: Boolean = logger.isDebugEnabled
  inline def infoEnabled:  Boolean = logger.isInfoEnabled
  inline def warnEnabled:  Boolean = logger.isWarnEnabled
  inline def errorEnabled: Boolean = logger.isErrorEnabled

  /** Message only log
    */
  inline def info(message: => String): Unit = inline if infoEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      INFO_INT,
      message,
      null,
      null
    )
  inline def warn(message: => String): Unit = inline if warnEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      WARN_INT,
      message,
      null,
      null
    )
  inline def error(message: => String): Unit = inline if errorEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      ERROR_INT,
      message,
      null,
      null
    )
  inline def debug(message: => String): Unit = inline if debugEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      DEBUG_INT,
      message,
      null,
      null
    )
  inline def trace(message: => String): Unit = inline if traceEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      getClass.getName,
      TRACE_INT,
      message,
      null,
      null
    )

  /** Handle messages and exceptions
    */
  inline def info(message: => String, throwable: Throwable): Unit = inline if infoEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      INFO_INT,
      message,
      null,
      throwable
    )
  inline def warn(message: => String, throwable: Throwable): Unit = inline if warnEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      getClass.getName,
      WARN_INT,
      message,
      null,
      throwable
    )
  inline def error(message: => String, throwable: Throwable): Unit = inline if errorEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      ERROR_INT,
      message,
      null,
      throwable
    )
  inline def debug(message: => String, throwable: Throwable): Unit = inline if debugEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      DEBUG_INT,
      message,
      null,
      throwable
    )
  inline def trace(message: => String, throwable: Throwable): Unit = inline if traceEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      TRACE_INT,
      message,
      null,
      throwable
    )

  /** Handle messages and Array Object
    */
  inline def info(message: => String, argArray: Array[Object]): Unit = inline if infoEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      INFO_INT,
      message,
      argArray,
      null
    )
  inline def warn(message: => String, argArray: Array[Object]): Unit = inline if warnEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      WARN_INT,
      message,
      argArray,
      null
    )
  inline def error(message: => String, argArray: Array[Object]): Unit = inline if errorEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      ERROR_INT,
      message,
      argArray,
      null
    )
  inline def debug(message: => String, argArray: Array[Object]): Unit = inline if debugEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      DEBUG_INT,
      message,
      argArray,
      null
    )
  inline def trace(message: => String, argArray: Array[Object]): Unit = inline if traceEnabled then
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      TRACE_INT,
      message,
      argArray,
      null
    )
