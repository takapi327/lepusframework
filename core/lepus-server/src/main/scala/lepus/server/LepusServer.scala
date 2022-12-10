/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import com.google.inject.Injector

import cats.effect.{ IO, Resource, ResourceApp }

import com.comcast.ip4s.Port

import org.typelevel.log4cats.Logger as Log4catsLogger

import org.http4s.*
import org.http4s.server.Server
import org.http4s.ember.server.EmberServerBuilder

import lepus.logger.given
import Exception.*
import lepus.guice.inject.GuiceApplicationBuilder
import lepus.app.{ LepusApp, BuiltinModule }

private[lepus] object LepusServer extends ResourceApp.Forever, ServerBuilder[IO], ServerLogging[IO]:

  private val SERVER_ROUTES = "lepus.server.routes"

  def run(args: List[String]): Resource[IO, Unit] =

    val lepusApp: LepusApp[IO] = loadLepusApp()

    for
      given Injector <- GuiceApplicationBuilder.build[IO](new BuiltinModule)
      _              <- buildServer(lepusApp)
    yield ()

  def buildServer(app: LepusApp[IO]): Injector ?=> Resource[IO, Server] =
    var ember = EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(port.getOrElse(Defaults.portInt)).getOrElse(Defaults.port))
      .withHttpApp(app.router)
      .withErrorHandler(app.errorHandler)
      .withMaxConnections(maxConnections.getOrElse(Defaults.maxConnections))
      .withReceiveBufferSize(receiveBufferSize.getOrElse(Defaults.receiveBufferSize))
      .withMaxHeaderSize(maxHeaderSize.getOrElse(Defaults.maxHeaderSize))
      .withRequestHeaderReceiveTimeout(requestHeaderReceiveTimeout.getOrElse(Defaults.requestHeaderReceiveTimeout))
      .withIdleTimeout(idleTimeout.getOrElse(Defaults.idleTimeout))
      .withShutdownTimeout(shutdownTimeout.getOrElse(Defaults.shutdownTimeout))
      .withLogger(logger.asInstanceOf[Log4catsLogger[IO]])

    if enableHttp2.getOrElse(false) then ember = ember.withHttp2
    else ember                                 = ember.withoutHttp2

    if enableIPv6.nonEmpty then ember = ember.withHost(ipv6Address)
    else ember                        = ember.withHost(ipv4Address)

    ember.build

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
