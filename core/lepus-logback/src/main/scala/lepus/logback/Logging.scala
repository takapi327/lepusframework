/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logback

import java.util.UUID

import org.slf4j.{ LoggerFactory, MarkerFactory, Logger => Slf4jLogger }
import org.slf4j.spi.LocationAwareLogger
import LocationAwareLogger._

/** Used to write logs to specific system or application components. Inherit from the class or object to which the log
  * is to be planted.
  *
  * For example:
  * {{{
  *   object LoggingTest extends Logging {
  *     logger.info("Test log writing")
  *   }
  * }}}
  */
trait Logging {
  protected[this] lazy val logger: Logger = {
    val clazz = getClass.getName
    val name  = if (clazz endsWith "$") clazz.substring(0, clazz.length - 1) else clazz
    new Logger(LoggerFactory.getLogger(name))
  }

  protected[this] lazy val trackingLogger: TrackingLogger = {
    val clazz = getClass.getName
    val name  = if (clazz endsWith "$") clazz.substring(0, clazz.length - 1) else clazz
    new TrackingLogger(LoggerFactory.getLogger(name).asInstanceOf[LocationAwareLogger], name)
  }
}

/** The TrackingLogger object is used to record messages and track logs for a particular system or application
  * component.
  * @param slf4jLogger
  *   Logger of slf4j to write logs
  */
private[lepus] sealed class Logger(val slf4jLogger: Slf4jLogger) {

  @inline final def traceEnabled: Boolean = slf4jLogger.isTraceEnabled
  @inline final def debugEnabled: Boolean = slf4jLogger.isDebugEnabled
  @inline final def infoEnabled:  Boolean = slf4jLogger.isInfoEnabled
  @inline final def warnEnabled:  Boolean = slf4jLogger.isWarnEnabled
  @inline final def errorEnabled: Boolean = slf4jLogger.isErrorEnabled

  /** Message only log
    */
  @inline final def error(msg: => String): Unit = if (errorEnabled) slf4jLogger.error(msg)
  @inline final def warn(msg: => String):  Unit = if (warnEnabled) slf4jLogger.warn(msg)
  @inline final def info(msg: => String):  Unit = if (infoEnabled) slf4jLogger.info(msg)
  @inline final def debug(msg: => String): Unit = if (debugEnabled) slf4jLogger.debug(msg)
  @inline final def trace(msg: => String): Unit = if (traceEnabled) slf4jLogger.trace(msg)

  /** Handle messages and exceptions
    */
  @inline final def error(msg: => String, throwable: Throwable): Unit =
    if (errorEnabled) slf4jLogger.error(msg, throwable)
  @inline final def warn(msg: => String, throwable: Throwable): Unit =
    if (warnEnabled) slf4jLogger.warn(msg, throwable)
  @inline final def info(msg: => String, throwable: Throwable): Unit =
    if (infoEnabled) slf4jLogger.info(msg, throwable)
  @inline final def debug(msg: => String, throwable: Throwable): Unit =
    if (debugEnabled) slf4jLogger.debug(msg, throwable)
  @inline final def trace(msg: => String, throwable: Throwable): Unit =
    if (traceEnabled) slf4jLogger.trace(msg, throwable)

  /** Handle messages and Array Object
    */
  @inline final def error(msg: => String, argArray: Array[Object]): Unit =
    if (errorEnabled) slf4jLogger.error(msg, argArray)
  @inline final def warn(msg: => String, argArray: Array[Object]): Unit =
    if (warnEnabled) slf4jLogger.warn(msg, argArray)
  @inline final def info(msg: => String, argArray: Array[Object]): Unit =
    if (infoEnabled) slf4jLogger.info(msg, argArray)
  @inline final def debug(msg: => String, argArray: Array[Object]): Unit =
    if (debugEnabled) slf4jLogger.debug(msg, argArray)
  @inline final def trace(msg: => String, argArray: Array[Object]): Unit =
    if (traceEnabled) slf4jLogger.trace(msg, argArray)
}

/** The TrackingLogger object is used to record messages and track logs for a particular system or application
  * component.
  *
  * @param logger
  *   LocationAwareLogger of slf4j api to write logs
  * @param className
  *   Name of the class calling the log
  */
private[lepus] sealed class TrackingLogger(logger: LocationAwareLogger, className: String) {

  @inline final def traceEnabled: Boolean = logger.isTraceEnabled
  @inline final def debugEnabled: Boolean = logger.isDebugEnabled
  @inline final def infoEnabled:  Boolean = logger.isInfoEnabled
  @inline final def warnEnabled:  Boolean = logger.isWarnEnabled
  @inline final def errorEnabled: Boolean = logger.isErrorEnabled

  /** Message only log
    */
  @inline final def info(message: => String): Unit = if (infoEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      INFO_INT,
      message,
      null,
      null
    )
  @inline final def warn(message: => String): Unit = if (warnEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      WARN_INT,
      message,
      null,
      null
    )
  @inline final def error(message: => String): Unit = if (errorEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      ERROR_INT,
      message,
      null,
      null
    )
  @inline final def debug(message: => String): Unit = if (debugEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      DEBUG_INT,
      message,
      null,
      null
    )
  @inline final def trace(message: => String): Unit = if (traceEnabled)
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
  @inline final def info(message: => String, throwable: Throwable): Unit = if (infoEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      INFO_INT,
      message,
      null,
      throwable
    )
  @inline final def warn(message: => String, throwable: Throwable): Unit = if (warnEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      getClass.getName,
      WARN_INT,
      message,
      null,
      throwable
    )
  @inline final def error(message: => String, throwable: Throwable): Unit = if (errorEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      ERROR_INT,
      message,
      null,
      throwable
    )
  @inline final def debug(message: => String, throwable: Throwable): Unit = if (debugEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      DEBUG_INT,
      message,
      null,
      throwable
    )
  @inline final def trace(message: => String, throwable: Throwable): Unit = if (traceEnabled)
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
  @inline final def info(message: => String, argArray: Array[Object]): Unit = if (infoEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      INFO_INT,
      message,
      argArray,
      null
    )
  @inline final def warn(message: => String, argArray: Array[Object]): Unit = if (warnEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      WARN_INT,
      message,
      argArray,
      null
    )
  @inline final def error(message: => String, argArray: Array[Object]): Unit = if (errorEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      ERROR_INT,
      message,
      argArray,
      null
    )
  @inline final def debug(message: => String, argArray: Array[Object]): Unit = if (debugEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      DEBUG_INT,
      message,
      argArray,
      null
    )
  @inline final def trace(message: => String, argArray: Array[Object]): Unit = if (traceEnabled)
    logger.log(
      MarkerFactory.getMarker(UUID.randomUUID.toString.replace("-", "")),
      className,
      TRACE_INT,
      message,
      argArray,
      null
    )
}
