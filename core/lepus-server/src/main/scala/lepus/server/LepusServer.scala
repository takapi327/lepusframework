/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import com.google.inject.Injector

import org.typelevel.vault.Vault

import cats.Functor
import cats.data.Kleisli
import cats.syntax.all.*

import cats.effect.{ IO, Resource, ResourceApp }

import org.typelevel.log4cats.slf4j.Slf4jLogger

import org.http4s.*

import lepus.core.util.Configuration
import Exception.*
import lepus.guice.inject.GuiceApplicationBuilder
import lepus.app.{ LepusApp, BuiltinModule }
import lepus.app.session.*

private[lepus] object LepusServer extends ResourceApp.Forever:

  private val SERVER_ROUTES = "lepus.server.routes"

  private val config: Configuration = Configuration.load()

  def run(args: List[String]): Resource[IO, Unit] =

    val lepusApp: LepusApp[IO] = loadLepusApp()

    for
      storage        <- Resource.eval(SessionStorage.default[IO, Vault]())
      given Injector <- GuiceApplicationBuilder.build[IO](new BuiltinModule)
      logger         <- Resource.eval(Slf4jLogger.create[IO])
      _              <- ServerBuilder.Ember[IO](logger).buildServer(
        app          = SessionMiddleware.fromConfig[IO, Vault](storage)(transFormRoutes(lepusApp.router)).orNotFound,
        errorHandler = lepusApp.errorHandler
      )
    yield ()

  private def transFormRoutes[F[_]: Functor](routes: HttpRoutes[F]): SessionMiddleware.SessionRoutes[Option[Vault], F] =
    Kleisli { (contextRequest: ContextRequest[F, Option[Vault]]) =>
      val initVault = contextRequest.context.fold(contextRequest.req.attributes)(context =>
        contextRequest.req.attributes ++ context
      )
      routes.run(contextRequest.req.withAttributes(initVault))
        .map { response =>
          val outContext =
            contextRequest.context.fold(response.attributes)(context => response.attributes ++ context)
          outContext.lookup(SessionReset.key)
            .fold(
              outContext.lookup(SessionRemove.key)
                .fold(
                  ContextResponse(outContext.some, response.withAttributes(outContext))
                )(toRemove =>
                  ContextResponse(
                    toRemove.list.foldLeft(outContext) { case (v, k) => v.delete(k) }.some,
                    response.withAttributes(outContext)
                  )
                )
            )(_ => ContextResponse(None, response.withAttributes(outContext)))
        }
    }

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
