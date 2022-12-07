/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import scala.concurrent.duration.*
import scala.language.reflectiveCalls

import com.google.inject.Injector

import cats.Semigroup
import cats.data.NonEmptyList
import cats.syntax.semigroupk.*

import cats.effect.*
import cats.effect.std.Console

import com.comcast.ip4s.*

import org.typelevel.log4cats.Logger as Log4catsLogger

import org.http4s.*
import org.http4s.HttpRoutes as Http4sRoutes
import org.http4s.server.Server
import org.http4s.ember.server.EmberServerBuilder

import lepus.logger.given
import lepus.core.util.Configuration
import lepus.router.{ *, given }
import Exception.*
import lepus.guice.inject.GuiceApplicationBuilder

private[lepus] object LepusServer extends ResourceApp.Forever, ServerInterpreter[IO], ServerLogging[IO]:

  private val SERVER_PORT                           = "lepus.server.port"
  private val SERVER_HOST                           = "lepus.server.host"
  private val SERVER_ROUTES                         = "lepus.server.routes"
  private val SERVER_MAX_CONNECTIONS                = "lepus.server.max_connections"
  private val SERVER_RECEIVE_BUFFER_SIZE            = "lepus.server.receive_buffer_size"
  private val SERVER_MAX_HEADER_SIZE                = "lepus.server.max_header_size"
  private val SERVER_REQUEST_HEADER_RECEIVE_TIMEOUT = "lepus.server.request_header_receive_timeout"
  private val SERVER_IDLE_TIMEOUT                   = "lepus.server.idle_timeout"
  private val SERVER_SHUTDOWN_TIMEOUT               = "lepus.server.shutdown_timeout"

  private val config: Configuration = Configuration.load()

  private val port:              Int         = config.get[Int](SERVER_PORT)
  private val host:              String      = config.get[String](SERVER_HOST)
  private val maxConnections:    Option[Int] = config.get[Option[Int]](SERVER_MAX_CONNECTIONS)
  private val receiveBufferSize: Option[Int] = config.get[Option[Int]](SERVER_RECEIVE_BUFFER_SIZE)
  private val maxHeaderSize:     Option[Int] = config.get[Option[Int]](SERVER_MAX_HEADER_SIZE)
  private val requestHeaderReceiveTimeout: Option[Duration] =
    config.get[Option[Duration]](SERVER_REQUEST_HEADER_RECEIVE_TIMEOUT)
  private val idleTimeout:     Option[Duration] = config.get[Option[Duration]](SERVER_IDLE_TIMEOUT)
  private val shutdownTimeout: Option[Duration] = config.get[Option[Duration]](SERVER_SHUTDOWN_TIMEOUT)

  given Semigroup[Http4sRoutes[IO]] = _ combineK _

  def run(args: List[String]): Resource[IO, Unit] =

    val lepusApp: LepusApp[IO] = loadLepusApp()

    for
      given Injector <- GuiceApplicationBuilder.build[IO]
      _              <- buildServer(host, port, lepusApp)
    yield ()

  private def buildApp(
    lepusApp: LepusApp[IO]
  )(using Injector): HttpApp[IO] =
    lepusApp.routes match
      case app: HttpApp[IO] => app
      case app: NonEmptyList[Routing[IO]] =>
        (lepusApp.cors match
          case Some(cors) =>
            app.map {
              case (endpoint, router) => cors(bindFromRequest(router.routes, endpoint))
            }
          case None =>
            app.map {
              case (endpoint, router) =>
                router.cors match
                  case Some(cors) => cors.apply(bindFromRequest(router.routes, endpoint))
                  case None       => bindFromRequest(router.routes, endpoint)
            }
        ).reduce.orNotFound

  private def buildServer(
    host: String,
    port: Int,
    app:  LepusApp[IO]
  )(using Injector): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(Ipv4Address.fromString(host).getOrElse(Defaults.host))
      .withPort(Port.fromInt(port).getOrElse(Defaults.port))
      .withHttpApp(buildApp(app))
      .withErrorHandler(app.errorHandler)
      .withMaxConnections(maxConnections.getOrElse(Defaults.maxConnections))
      .withReceiveBufferSize(receiveBufferSize.getOrElse(Defaults.receiveBufferSize))
      .withMaxHeaderSize(maxHeaderSize.getOrElse(Defaults.maxHeaderSize))
      .withRequestHeaderReceiveTimeout(requestHeaderReceiveTimeout.getOrElse(Defaults.requestHeaderReceiveTimeout))
      .withIdleTimeout(idleTimeout.getOrElse(Defaults.idleTimeout))
      .withShutdownTimeout(shutdownTimeout.getOrElse(Defaults.shutdownTimeout))
      .withLogger(logger.asInstanceOf[Log4catsLogger[IO]])
      .build

  private def loadLepusApp(): LepusApp[IO] =
    val routesClassName: String = config.get[String](SERVER_ROUTES)
    val routeClass: Class[?] =
      try ClassLoader.getSystemClassLoader.loadClass(routesClassName + "$")
      catch
        case ex: ClassNotFoundException =>
          throw ServerStartException(s"Couldn't find LepusApp class '$routesClassName'", Some(ex))

    if !classOf[LepusApp[IO]].isAssignableFrom(routeClass) then
      throw ServerStartException(s"Class ${ routeClass.getName } must implement LepusApp interface")

    val constructor =
      try routeClass.getField("MODULE$").get(null).asInstanceOf[LepusApp[IO]]
      catch
        case ex: NoSuchMethodException =>
          throw ServerStartException(
            s"LepusApp class ${ routeClass.getName } must have a public default constructor",
            Some(ex)
          )

    constructor

  private object Defaults:

    /** Default host */
    val host: Host = ipv4"0.0.0.0"

    /** Default port */
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
