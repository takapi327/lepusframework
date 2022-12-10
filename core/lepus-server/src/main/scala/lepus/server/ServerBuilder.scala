/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.server

import java.net.InetAddress

import scala.concurrent.duration.*

import com.google.inject.Injector

import com.comcast.ip4s.*

import cats.effect.Resource

import org.http4s.*
import org.http4s.server.Server

import lepus.core.util.Configuration

import lepus.app.LepusApp

trait ServerBuilder[F[_]]:

  private val SERVER_HOST = "lepus.server.host"
  private val SERVER_PORT   = "lepus.server.port"
  private val SERVER_MAX_CONNECTIONS = "lepus.server.max_connections"
  private val SERVER_RECEIVE_BUFFER_SIZE = "lepus.server.receive_buffer_size"
  private val SERVER_MAX_HEADER_SIZE = "lepus.server.max_header_size"
  private val SERVER_REQUEST_HEADER_RECEIVE_TIMEOUT = "lepus.server.request_header_receive_timeout"
  private val SERVER_IDLE_TIMEOUT = "lepus.server.idle_timeout"
  private val SERVER_SHUTDOWN_TIMEOUT = "lepus.server.shutdown_timeout"
  private val SERVER_ENABLE_HTTP2 = "lepus.server.http2"
  private val SERVER_ENABLE_IPV6 = "lepus.server.ipv6"

  protected val config: Configuration = Configuration.load()

  protected val port: Option[Int] = config.get[Option[Int]](SERVER_PORT)
  protected val host: Option[String] = config.get[Option[String]](SERVER_HOST)
  protected val maxConnections: Option[Int] = config.get[Option[Int]](SERVER_MAX_CONNECTIONS)
  protected val receiveBufferSize: Option[Int] = config.get[Option[Int]](SERVER_RECEIVE_BUFFER_SIZE)
  protected val maxHeaderSize: Option[Int] = config.get[Option[Int]](SERVER_MAX_HEADER_SIZE)
  protected val requestHeaderReceiveTimeout: Option[Duration] =
    config.get[Option[Duration]](SERVER_REQUEST_HEADER_RECEIVE_TIMEOUT)
  protected val idleTimeout: Option[Duration] = config.get[Option[Duration]](SERVER_IDLE_TIMEOUT)
  protected val shutdownTimeout: Option[Duration] = config.get[Option[Duration]](SERVER_SHUTDOWN_TIMEOUT)
  protected val enableHttp2: Option[Boolean] = config.get[Option[Boolean]](SERVER_ENABLE_HTTP2)
  protected val enableIPv6: Option[String] = config.get[Option[String]](SERVER_ENABLE_IPV6)

  protected lazy val ipv4Address: Ipv4Address =
    Ipv4Address.fromString(host.getOrElse(Defaults.IPv4Host)).getOrElse(Defaults.host)
  protected lazy val ipv6Address: Ipv6Address =
    enableIPv6.flatMap(Ipv6Address.fromString).getOrElse(throw new IllegalArgumentException(s"$enableIPv6 Did not match IPv6 format."))

  def buildServer(app: LepusApp[F]): Injector ?=> Resource[F, Server]

  protected object Defaults:

    /** Default host */
    val IPv4Host: String =
      InetAddress.getByAddress("localhost", Array[Byte](127, 0, 0, 1)).getHostAddress
    val IPv6Host: String =
      InetAddress
        .getByAddress("localhost", Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1))
        .getHostAddress
    val host: Ipv4Address = ipv4"0.0.0.0"

    /** Default port */
    val portInt: Int  = 5555
    val port: Port = port"5555"

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
