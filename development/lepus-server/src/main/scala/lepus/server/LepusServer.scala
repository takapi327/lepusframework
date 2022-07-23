/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import scala.language.reflectiveCalls

import cats.effect.*

import org.http4s.blaze.server.BlazeServerBuilder

import lepus.core.util.Configuration
import lepus.router.{ *, given }
import Exception.*

object LepusServer extends IOApp, ServerInterpreter[IO]:

  private val SERVER_PORT   = "lepus.server.port"
  private val SERVER_HOST   = "lepus.server.host"
  private val SERVER_ROUTES = "lepus.server.routes"

  val config = Configuration.load()

  def run(args: List[String]): IO[ExitCode] =
    val port: Int    = config.get[Int](SERVER_PORT)
    val host: String = config.get[String](SERVER_HOST)

    val routerProvider: RouterProvider[IO] = loadRouterProvider()

    val httpApp = routerProvider.routes.map(v => {
      bindFromRequest(v.routes, v.endpoint)
    }).reduce

    BlazeServerBuilder[IO]
      .bindHttp(port, host)
      .withHttpApp(httpApp.orNotFound)
      .withoutBanner
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

  private def loadRouterProvider(): RouterProvider[IO] =
    val routesClassName: String = config.get[String](SERVER_ROUTES)
    val routeClass: Class[_] =
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
