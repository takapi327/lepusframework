/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.app.session

import cats.syntax.all.*

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import org.typelevel.ci.*

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*

import org.specs2.mutable.Specification

import lepus.app.syntax.*

case class Session(name: String)

object SessionMiddlewareTest extends Specification:

  val emptyStorage: IO[SessionStorage[IO, Session]] = SessionStorage.default[IO, Session]()

  val sessionRoutes: SessionRoutes[IO, Option[Session]] = SessionRoutes.of[IO, Option[Session]] {
    case GET -> Root / "session" / "init" as _ =>
      Ok("Session init").withContext(Some(Session("Init")))
    case GET -> Root / "session" / "reset" as _ => Ok("Session reset").withContext(None)
    case GET -> Root / "session" / "update" as session =>
      Ok("Session update").withContext(session.map(_.copy(name = "Updated")))
    case GET -> Root / "session" / "both" / "update" as session =>
      Ok("Session update").withContext(session.map(_.copy(name = "Both Updated")))
  }

  "Testing the SessionMiddleware" should {
    "If the storage identifier does not exist in the cookie, a new identifier is generated and stored in the response header." in {
      val result = for
        storage <- emptyStorage
        routes = SessionMiddleware.fromConfig[IO, Session](storage)(sessionRoutes)
        res <- routes.run(Request().withUri(uri"session/init")).value
        id = selectIdFromResponseHeader(res)
        session <- storage.get(SessionIdentifier(id.getOrElse("")))
      yield id.isDefined and session.isDefined and session.contains(Session("Init"))

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
      yield id.isDefined and old.isDefined and old.contains(Session("Init")) and session.isEmpty

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
      yield id.isDefined and old.isDefined and old.contains(Session("Init")) and session.isDefined and session
        .contains(Session("Updated")) and old != session

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

    "Even when simultaneous accesses are made, the identifiers generated will be different values." in {
      val result =
        val initRequest: Request[IO] = Request().withUri(uri"session/init")
        for
          storage <- emptyStorage
          routes = SessionMiddleware.fromConfig[IO, Session](storage)(sessionRoutes)
          res <- (routes.run(initRequest).value, routes.run(initRequest).value)
                   .parMapN((a1, a2) => selectIdFromResponseHeader(a1) !== selectIdFromResponseHeader(a2))
        yield res

      result.unsafeRunSync()
    }

    "If the identifiers are different, they will not overwrite each other's values even if Session information is updated at the same time." in {

      val initRequest: Request[IO] = Request().withUri(uri"session/init")

      val result =
        for
          storage <- emptyStorage
          routes = SessionMiddleware.fromConfig[IO, Session](storage)(sessionRoutes)
          idTuple <- (routes.run(initRequest).value, routes.run(initRequest).value)
                       .parMapN((a1, a2) =>
                         (
                           selectIdFromResponseHeader(a1).map(SessionIdentifier(_)),
                           selectIdFromResponseHeader(a2).map(SessionIdentifier(_))
                         )
                       )
          _ <- (
                 update(routes, uri"session/update", idTuple._1.get),
                 update(routes, uri"session/both/update", idTuple._2.get)
               )
                 .parMapN((_, _) => ())
          a1 <- storage.get(idTuple._1.get)
          a2 <- storage.get(idTuple._2.get)
        yield a1.nonEmpty and a1.contains(Session("Updated")) and
          a2.nonEmpty and a2.contains(Session("Both Updated")) and
          (a1 !== a2)

      result.unsafeRunSync()
    }

    "If the Session is updated at the same time with the same identifier, the updated value will contain the result of the update process that took place even a little later." in {
      val initRequest: Request[IO] = Request().withUri(uri"session/init")

      val result =
        for
          storage <- emptyStorage
          routes = SessionMiddleware.fromConfig[IO, Session](storage)(sessionRoutes)
          res <- routes.run(initRequest).value
          id = selectIdFromResponseHeader(res).map(SessionIdentifier(_))
          _ <- (update(routes, uri"session/update", id.get), update(routes, uri"session/both/update", id.get))
                 .parMapN((_, _) => ())
          session <- storage.get(id.get)
        yield session.nonEmpty and (session.contains(Session("Both Updated")) or session.contains(Session("Updated")))

      result.unsafeRunSync()
    }
  }

  private def update(routes: HttpRoutes[IO], uri: Uri, id: SessionIdentifier): IO[Option[Response[IO]]] =
    routes.run(Request().withUri(uri).addCookie("LEPUS_SESSION_TEST", id.value)).value

  private def selectIdFromResponseHeader(
    response: Option[Response[IO]],
    key:      String = "LEPUS_SESSION_TEST"
  ): Option[String] =
    response
      .flatMap(_.headers.get(CIString("Set-Cookie")))
      .flatMap(_.head.value.split(';').headOption.map(_.replace(s"$key=", "")))
