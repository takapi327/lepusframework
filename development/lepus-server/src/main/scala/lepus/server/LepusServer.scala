/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import scala.language.reflectiveCalls

import cats.effect.*
import cats.effect.std.Console

import com.comcast.ip4s.*

import org.typelevel.log4cats.Logger as Log4catsLogger

import org.http4s.*
import org.http4s.HttpRoutes as Http4sRoutes
import org.http4s.server.Server
import org.http4s.ember.server.EmberServerBuilder

import doobie.Transactor

import lepus.logger.given
import lepus.core.util.Configuration
import lepus.router.{ *, given }
import Exception.*
import lepus.database.{ DatabaseBuilder, DatabaseConfig, DataSource, DBTransactor }

private[lepus] object LepusServer extends ResourceApp.Forever, ServerInterpreter[IO], ServerLogging[IO]:

  private val SERVER_PORT   = "lepus.server.port"
  private val SERVER_HOST   = "lepus.server.host"
  private val SERVER_ROUTES = "lepus.server.routes"

  val config = Configuration.load()

  def run(args: List[String]): Resource[IO, Unit] =
    val port: Int    = config.get[Int](SERVER_PORT)
    val host: String = config.get[String](SERVER_HOST)

    val lepusApp: LepusApp[IO] = loadLepusApp()

    for
      given DBTransactor[IO] <- buildDatabases[IO](lepusApp.databases)
      _                      <- buildServer(host, port, lepusApp)
    yield ()

  private def buildDatabases[F[_]: Sync: Async: Console](
    databases: Set[DatabaseConfig]
  ): Resource[F, DBTransactor[F]] =
    val default = Resource.eval(Sync[F].delay(Map.empty[DataSource, Transactor[F]]))
    databases.flatMap(_.dataSource.toList).foldLeft(default) { (resource, db) =>
      for
        map <- resource
        xa  <- DatabaseBuilder(db).resource
      yield map + (db -> xa)
    }

  private def buildApp(
    lepusApp: LepusApp[IO]
  )(using DBTransactor[IO]): Http4sRoutes[IO] =
    (lepusApp.cors match
      case Some(cors) =>
        lepusApp.routes.map {
          case (endpoint, router) => cors(bindFromRequest(router.routes, endpoint))
        }
      case None =>
        lepusApp.routes.map {
          case (endpoint, router) =>
            router.cors match
              case Some(cors) => cors.apply(bindFromRequest(router.routes, endpoint))
              case None       => bindFromRequest(router.routes, endpoint)
        }
    ).reduce

  private def buildServer(
    host: String,
    port: Int,
    app:  LepusApp[IO]
  )(using DBTransactor[IO]): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(Ipv4Address.fromString(host).getOrElse(ipv4"0.0.0.0"))
      .withPort(Port.fromInt(port).getOrElse(port"5555"))
      .withHttpApp(buildApp(app).orNotFound)
      .withErrorHandler(app.errorHandler)
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
