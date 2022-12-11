/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import java.net.InetAddress

import scala.concurrent.duration.*

import com.google.inject.Injector

import com.comcast.ip4s.*

import cats.effect.Resource

import org.typelevel.log4cats.Logger as Log4catsLogger

import org.http4s.*
import org.http4s.server.Server

import lepus.core.util.Configuration

import lepus.app.LepusApp
import lepus.logger.{ Logger, DefaultLogging, given }

import ServerBuilder.Defaults

/** trait to configure the server to start the server used by the application.
  *
  * @tparam F
  *   the effect type.
  */
trait ServerBuilder[F[_]]:

  private val SERVER_HOST_IPV4                      = "lepus.server.host.ipv4"
  private val SERVER_HOST_IPV6                      = "lepus.server.host.ipv6"
  private val SERVER_PORT                           = "lepus.server.port"
  private val SERVER_MAX_CONNECTIONS                = "lepus.server.max_connections"
  private val SERVER_RECEIVE_BUFFER_SIZE            = "lepus.server.receive_buffer_size"
  private val SERVER_MAX_HEADER_SIZE                = "lepus.server.max_header_size"
  private val SERVER_REQUEST_HEADER_RECEIVE_TIMEOUT = "lepus.server.request_header_receive_timeout"
  private val SERVER_IDLE_TIMEOUT                   = "lepus.server.idle_timeout"
  private val SERVER_SHUTDOWN_TIMEOUT               = "lepus.server.shutdown_timeout"
  private val SERVER_ENABLE_HTTP2                   = "lepus.server.http2"

  protected val config: Configuration = Configuration.load()

  protected val port:              Option[Int]    = config.get[Option[Int]](SERVER_PORT)
  protected val hostIPv4:          Option[String] = config.get[Option[String]](SERVER_HOST_IPV4)
  protected val hostIPv6:          Option[String] = config.get[Option[String]](SERVER_HOST_IPV6)
  protected val maxConnections:    Option[Int]    = config.get[Option[Int]](SERVER_MAX_CONNECTIONS)
  protected val receiveBufferSize: Option[Int]    = config.get[Option[Int]](SERVER_RECEIVE_BUFFER_SIZE)
  protected val maxHeaderSize:     Option[Int]    = config.get[Option[Int]](SERVER_MAX_HEADER_SIZE)
  protected val requestHeaderReceiveTimeout: Option[Duration] =
    config.get[Option[Duration]](SERVER_REQUEST_HEADER_RECEIVE_TIMEOUT)
  protected val idleTimeout:     Option[Duration] = config.get[Option[Duration]](SERVER_IDLE_TIMEOUT)
  protected val shutdownTimeout: Option[Duration] = config.get[Option[Duration]](SERVER_SHUTDOWN_TIMEOUT)
  protected val enableHttp2:     Option[Boolean]  = config.get[Option[Boolean]](SERVER_ENABLE_HTTP2)

  def buildServer(app: LepusApp[F], log4catsLogger: Log4catsLogger[F]): Injector ?=> Resource[F, Server]

object ServerBuilder:

  import cats.Monad
  import cats.effect.Async
  import cats.effect.kernel.Clock
  import cats.effect.std.Console
  import org.http4s.ember.server.EmberServerBuilder

  /** Object to start http4s EmberServer
    *
    * TODO: Scheduled to be cut out into modules for each type of server
    */
  object Ember:

    def apply[F[_]: Async: Monad: Clock: Console]: ServerBuilder[F] =
      new ServerBuilder[F] with DefaultLogging:

        private def buildIPv4Address(ipv4: String): Host =
          Ipv4Address
            .fromString(ipv4)
            .orElse({
              logger.warn(
                s"""
                   |===============================================================================
                   |  The specified address $ipv4 did not match the IPv4 format.
                   |  The application was started with the default IPv4 address ${ Defaults.IPv4Host }.
                   |===============================================================================
                   |""".stripMargin
              )
              Ipv4Address.fromString(Defaults.IPv4Host)
            })
            .getOrElse({
              logger.warn(
                s"""
                   |===============================================================================
                   |  The specified address $ipv4 did not match the IPv4 format.
                   |  The application was started with the default IPv4 address ${ Defaults.ipv4Address }.
                   |===============================================================================
                   |""".stripMargin
              )
              Defaults.ipv4Address
            })

        private def buildIPv6Address(ipv6: String): Host =
          Ipv6Address
            .fromString(ipv6)
            .orElse({
              logger.warn(
                s"""
                   |===============================================================================
                   |  The specified address $ipv6 did not match the IPv6 format.
                   |  The application was started with the default IPv6 address ${ Defaults.IPv6Host }.
                   |===============================================================================
                   |""".stripMargin
              )
              Ipv6Address.fromString(Defaults.IPv6Host)
            })
            .getOrElse({
              logger.warn(
                s"""
                   |===============================================================================
                   |  The specified address $ipv6 did not match the IPv6 format.
                   |  The application was started with the default IPv6 address ${ Defaults.ipv6Address }.
                   |===============================================================================
                   |""".stripMargin
              )
              Defaults.ipv6Address
            })

        /** Get either IPv4/IPv6 Host depending on the configuration
          */
        protected lazy val host: Host =
          (hostIPv4, hostIPv6) match
            case (Some(ipv4), None) => buildIPv4Address(ipv4)
            case (None, Some(ipv6)) => buildIPv6Address(ipv6)
            case (Some(ipv4), Some(ipv6)) =>
              logger.warn(
                s"""
                   |===============================================================================
                   |  Both IPv4 and IPv6 settings were detected.
                   |  IPv4 Address: $ipv4
                   |  IPv6 Address: $ipv6
                   |
                   |  If both settings are present, the IPv6 setting is wired.
                   |  If you want to start the application with an IPv4 address, remove the IPv6 setting.
                   |===============================================================================
                   |""".stripMargin
              )
              buildIPv6Address(ipv6)
            case (None, None) => Defaults.ipv4Address

        def buildServer(app: LepusApp[F], log4catsLogger: Log4catsLogger[F]): Injector ?=> Resource[F, Server] =
          var ember = EmberServerBuilder
            .default[F]
            .withHost(host)
            .withPort(Port.fromInt(port.getOrElse(Defaults.portInt)).getOrElse(Defaults.port))
            .withHttpApp(app.router)
            .withErrorHandler(app.errorHandler)
            .withMaxConnections(maxConnections.getOrElse(Defaults.maxConnections))
            .withReceiveBufferSize(receiveBufferSize.getOrElse(Defaults.receiveBufferSize))
            .withMaxHeaderSize(maxHeaderSize.getOrElse(Defaults.maxHeaderSize))
            .withRequestHeaderReceiveTimeout(
              requestHeaderReceiveTimeout.getOrElse(Defaults.requestHeaderReceiveTimeout)
            )
            .withIdleTimeout(idleTimeout.getOrElse(Defaults.idleTimeout))
            .withShutdownTimeout(shutdownTimeout.getOrElse(Defaults.shutdownTimeout))
            .withLogger(log4catsLogger)

          if enableHttp2.getOrElse(false) then ember = ember.withHttp2
          else ember                                 = ember.withoutHttp2

          logger.debug(
            s"""
               |===============================================================================
               |List of EmberServer startup settings
               |
               |host:                           ${ ember.host.get }
               |port:                           ${ ember.port }
               |max connections:                ${ ember.maxConnections }
               |receive buffer size:            ${ ember.receiveBufferSize }
               |max header size:                ${ ember.maxHeaderSize }
               |request header receive timeout: ${ ember.requestHeaderReceiveTimeout }
               |idle timeout:                   ${ ember.idleTimeout }
               |shutdown timeout:               ${ ember.shutdownTimeout }
               |enable Http2:                   ${ enableHttp2.getOrElse(false) }
               |
               |===============================================================================
               |""".stripMargin
          )

          ember.build

  object Defaults:

    /** Default IPv4/IPv6 host */
    val IPv4Host: String =
      InetAddress.getByAddress("localhost", Array[Byte](127, 0, 0, 1)).getHostAddress
    val IPv6Host: String =
      InetAddress
        .getByAddress("localhost", Array[Byte](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1))
        .getHostAddress
    val ipv4Address: Ipv4Address = ipv4"0.0.0.0"
    val ipv6Address: Ipv6Address = ipv6"0:0:0:0:0:0:0:1"

    /** Default port */
    val portInt: Int  = 5555
    val port:    Port = port"5555"

    /** Default max connections */
    val maxConnections: Int = 1024

    /** Default receive Buffer Size */
    val receiveBufferSize: Int = 256 * 1024

    /** Default max size of all headers */
    val maxHeaderSize: Int = 40 * 1024

    /** Default request Header Receive Timeout */
    val requestHeaderReceiveTimeout: Duration = 5.seconds

    /** Default Idle Timeout */
    val idleTimeout: Duration = 60.seconds

    /** The time to wait for a graceful shutdown */
    val shutdownTimeout: Duration = 30.seconds
