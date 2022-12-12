/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import com.google.inject.Injector

import cats.effect.{ IO, Resource, ResourceApp }

import org.typelevel.log4cats.Logger as Log4catsLogger

import lepus.core.util.Configuration
import lepus.logger.given
import Exception.*
import lepus.guice.inject.GuiceApplicationBuilder
import lepus.app.{ LepusApp, BuiltinModule }

private[lepus] object LepusServer extends ResourceApp.Forever, ServerLogging[IO]:

  private val SERVER_ROUTES = "lepus.server.routes"

  private val config: Configuration = Configuration.load()

  def run(args: List[String]): Resource[IO, Unit] =

    val lepusApp: LepusApp[IO] = loadLepusApp()

    for
      given Injector <- GuiceApplicationBuilder.build[IO](new BuiltinModule)
      _              <- ServerBuilder.Ember[IO].buildServer(lepusApp, logger.asInstanceOf[Log4catsLogger[IO]])
    yield ()

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
