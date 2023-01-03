/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.app.session

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import org.typelevel.ci.*

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*

import org.specs2.mutable.Specification

case class Session(name: String)

object SessionMiddlewareTest extends Specification:

  val emptyStorage: IO[SessionStorage[IO, Session]] = SessionStorage.default[IO, Session]()

  val sessionRoutes: SessionRoutes[IO, Option[Session]] = SessionRoutes.of[IO, Option[Session]] {
    case GET -> Root / "session" / "init" as _ =>
      Ok("Session init").map(resp => ContextResponse(Some(Session("Init")), resp))
    case GET -> Root / "session" / "reset" as _ => Ok("Session reset").map(resp => ContextResponse(None, resp))
    case GET -> Root / "session" / "update" as session =>
      Ok("Session update").map(resp => ContextResponse(session.map(_.copy(name = "Updated")), resp))
  }

  "Testing the SessionMiddleware" should {
    "If the storage identifier does not exist in the cookie, a new identifier is generated and stored in the response header." in {
      val result = for
        storage <- emptyStorage
        routes = SessionMiddleware.fromConfig[IO, Session](storage)(sessionRoutes)
        res <- routes.run(Request().withUri(uri"session/init")).value
        id = selectIdFromResponseHeader(res)
        session <- storage.get(SessionIdentifier(id.getOrElse("")))
      yield id.isDefined and session.isDefined and session.map(_.name).contains("Init")

      result.unsafeRunSync()
    }

    "If ContextResponse is set to None with an identifier, the value stored in storage will also be None." in {
      val result = for
        storage <- emptyStorage
        routes = SessionMiddleware.fromConfig[IO, Session](storage)(sessionRoutes)
        res <- routes.run(Request().withUri(uri"session/init")).value
        id = selectIdFromResponseHeader(res)
        old <- storage.get(SessionIdentifier(id.getOrElse("")))
        _   <- routes.run(Request().withUri(uri"session/reset").addCookie("LEPUS_SESSION_TEST", id.getOrElse(""))).value
        session <- storage.get(SessionIdentifier(id.getOrElse("")))
      yield id.isDefined and old.isDefined and old.map(_.name).contains("Init") and session.isEmpty

      result.unsafeRunSync()
    }

    "With the identifier in place, the value stored in storage can be updated." in {
      val result = for
        storage <- emptyStorage
        routes = SessionMiddleware.fromConfig[IO, Session](storage)(sessionRoutes)
        res <- routes.run(Request().withUri(uri"session/init")).value
        id = selectIdFromResponseHeader(res)
        old <- storage.get(SessionIdentifier(id.getOrElse("")))
        _ <- routes.run(Request().withUri(uri"session/update").addCookie("LEPUS_SESSION_TEST", id.getOrElse(""))).value
        session <- storage.get(SessionIdentifier(id.getOrElse("")))
      yield id.isDefined and old.isDefined and old.map(_.name).contains("Init") and session.isDefined and session
        .map(_.name)
        .contains("Updated") and old != session

      result.unsafeRunSync()
    }

    "If a non-existent identifier is used, the value stored in storage cannot be retrieved." in {
      val result = for
        storage <- emptyStorage
        routes = SessionMiddleware.fromConfig[IO, Session](storage)(sessionRoutes)
        res <- routes.run(Request().withUri(uri"session/init")).value
        id = selectIdFromResponseHeader(res)
        noneOld <- storage.get(SessionIdentifier("hogehoge"))
        success <- storage.get(SessionIdentifier(id.getOrElse("")))
        _       <- routes.run(Request().withUri(uri"session/update").addCookie("LEPUS_SESSION_TEST", "hogehoge")).value
        noneNew <- storage.get(SessionIdentifier("hogehoge"))
      yield noneOld.isEmpty and noneNew.isEmpty and success.isDefined

      result.unsafeRunSync()
    }
  }

  private def selectIdFromResponseHeader(
    response: Option[Response[IO]],
    key:      String = "LEPUS_SESSION_TEST"
  ): Option[String] =
    response
      .flatMap(_.headers.get(CIString("Set-Cookie")))
      .flatMap(_.head.value.split(';').headOption.map(_.replace(s"$key=", "")))
