/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import scala.language.reflectiveCalls

import cats.effect.*
import cats.effect.std.Console

import com.comcast.ip4s.*

import org.legogroup.woof.{ Output, Filter, Printer }
import org.legogroup.woof.given
import org.legogroup.woof.Logger.StringLocal

import org.http4s.*
import org.http4s.HttpRoutes as Http4sRoutes
import org.http4s.server.Server
import org.http4s.ember.server.EmberServerBuilder

import lepus.core.util.Configuration
import lepus.router.{ *, given }
import lepus.logger.Logger
import Exception.*

private[lepus] object LepusServer extends IOApp, ServerInterpreter[IO]:

  private val SERVER_PORT   = "lepus.server.port"
  private val SERVER_HOST   = "lepus.server.host"
  private val SERVER_ROUTES = "lepus.server.routes"

  val config = Configuration.load()

  def run(args: List[String]): IO[ExitCode] =
    val port: Int    = config.get[Int](SERVER_PORT)
    val host: String = config.get[String](SERVER_HOST)

    val routerProvider: RouterProvider[IO] = loadRouterProvider()

    given Filter  = routerProvider.filter
    given Printer = routerProvider.printer

    (for
      given StringLocal[IO] <- routerProvider.local
      logger                <- IO.delay { ServerLogger(routerProvider.debugger) }
      httpApp               <- IO.delay { buildApp(routerProvider) }
      server                <- buildServer(host, port, httpApp.orNotFound, logger).use(_ => IO.never)
    yield server).as(ExitCode.Success)

  private def buildApp(
    routerProvider: RouterProvider[IO]
  )(using Filter, Printer, StringLocal[IO]): Http4sRoutes[IO] =
    given Logger[IO] = Logger[IO](routerProvider.debugger)
    (routerProvider.cors match
      case Some(cors) =>
        routerProvider.routes.map {
          case (endpoint, router) => cors(bindFromRequest(router.routes, endpoint))
        }
      case None =>
        routerProvider.routes.map {
          case (endpoint, router) =>
            router.cors match
              case Some(cors) => cors.apply(bindFromRequest(router.routes, endpoint))
              case None       => bindFromRequest(router.routes, endpoint)
        }
    ).reduce

  private def buildServer(
    host:   String,
    port:   Int,
    app:    HttpApp[IO],
    logger: ServerLogger[IO]
  ): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(Ipv4Address.fromString(host).getOrElse(ipv4"0.0.0.0"))
      .withPort(Port.fromInt(port).getOrElse(port"5555"))
      .withHttpApp(app)
      .withErrorHandler {
        case error =>
          logger
            .error(error)(s"Unexpected error: $error")
            .as(Response(Status.InternalServerError))
      }
      .withLogger(logger)
      .build

  private def loadRouterProvider(): RouterProvider[IO] =
    val routesClassName: String = config.get[String](SERVER_ROUTES)
    val routeClass: Class[?] =
      try ClassLoader.getSystemClassLoader.loadClass(routesClassName + "$")
      catch
        case ex: ClassNotFoundException =>
          throw ServerStartException(s"Couldn't find RouterProvider class '$routesClassName'", Some(ex))

    if !classOf[RouterProvider[IO]].isAssignableFrom(routeClass) then
      throw ServerStartException(s"Class ${ routeClass.getName } must implement RouterProvider interface")

    val constructor =
      try routeClass.getField("MODULE$").get(null).asInstanceOf[RouterProvider[IO]]
      catch
        case ex: NoSuchMethodException =>
          throw ServerStartException(
            s"RouterProvider class ${ routeClass.getName } must have a public default constructor",
            Some(ex)
          )

    constructor
