/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.app.session

import cats.{ Monad, Functor }
import cats.data.{ OptionT, Kleisli }
import cats.syntax.all.*

import cats.effect.Clock

import org.http4s.*
import org.http4s.headers.`Set-Cookie`

/** copied from http4s-session:
  * https://github.com/http4s/http4s-session/blob/main/core/src/main/scala/org/http4s/session/SessionMiddleware.scala
  */
private[lepus] object SessionMiddleware extends SessionConfigReader:

  type SessionRoutes[T, F[_]] = Kleisli[[A] =>> OptionT[F, A], ContextRequest[F, T], ContextResponse[F, T]]

  /** Methods for building Middleware from settings passed as arguments.
    *
    * @param storage
    *   Session Storage
    * @param sessionIdentifierName
    *   Session Identifier
    * @param httpOnly
    *   HttpOnly information to be set in cookies
    * @param secure
    *   Secure information to be set in cookies
    * @param domain
    *   Domain information to be set in cookies
    * @param path
    *   Path information to be set in cookies
    * @param sameSite
    *   SameSite information to be set in cookies
    * @param expiration
    *   Expiration information to be set in cookies
    * @param mergeOnChanged
    *   Model for context merging control
    * @param routes
    *   Route information using SessionStorage
    * @tparam F
    *   the effect type.
    * @tparam A
    *   Value managed by Session
    * @return
    *   A HttpRoutes
    */
  def apply[F[_]: Monad, A](
    storage:               SessionStorage[F, A],
    sessionIdentifierName: String = "LEPUS_SESSION",
    httpOnly:              Boolean = true,
    secure:                Boolean = true,
    domain:                Option[String] = Option.empty[String],
    path:                  Option[String] = None,
    sameSite:              SameSite = SameSite.Lax,
    expiration:            ExpirationManagement[F] = ExpirationManagement.Static(None, None),
    mergeOnChanged:        Option[MergeManagement[Option[A]]] = Option.empty[MergeManagement[Option[A]]]
  )(routes:                SessionRoutes[Option[A], F]): HttpRoutes[F] =
    val deleteCookie = generateDeleteCookie(sessionIdentifierName, domain, path, sameSite, secure, httpOnly)

    def sessionCookie(id: SessionIdentifier): F[`Set-Cookie`] =
      expiration match
        case ExpirationManagement.Static(maxAge, expires) =>
          `Set-Cookie`(
            ResponseCookie(
              name     = sessionIdentifierName,
              content  = id.value,
              expires  = expires,
              maxAge   = maxAge,
              domain   = domain,
              path     = path,
              sameSite = sameSite.some,
              secure   = secure,
              httpOnly = httpOnly
            )
          ).pure[F]
        case e @ ExpirationManagement.Dynamic(fromNow) =>
          HttpDate.current[F](Functor[F], e.clock).flatMap { now =>
            fromNow(now).map {
              case ExpirationManagement.Static(maxAge, expires) =>
                `Set-Cookie`(
                  ResponseCookie(
                    name     = sessionIdentifierName,
                    content  = id.value,
                    expires  = expires,
                    maxAge   = maxAge,
                    domain   = domain,
                    path     = path,
                    sameSite = sameSite.some,
                    secure   = secure,
                    httpOnly = httpOnly
                  )
                )
            }
          }

    Kleisli { (request: Request[F]) =>
      val sessionId = SessionIdentifier.extract(request, sessionIdentifierName)
      val session   = sessionId.flatTraverse(id => storage.get(id))

      for
        sessionOpt <- OptionT.liftF(session)
        response   <- routes(ContextRequest(sessionOpt, request))
        out <- OptionT.liftF((sessionId, response.context) match
                 case (None, None) => response.response.pure[F]
                 case (Some(id), Some(context)) =>
                   storage.modify(
                     id,
                     { now =>
                       val next: Option[A] = mergeOnChanged.fold(context.some) { mm =>
                         if !mm.eqv(sessionOpt, now) then mm.whenDifferent(now, context.some)
                         else context.some
                       }
                       (next, ())
                     }
                   ) >> sessionCookie(id).map(response.response.putHeaders(_))
                 case (None, Some(context)) =>
                   storage.sessionId.flatMap(id =>
                     storage.modify(
                       id,
                       { now =>
                         val next: Option[A] = mergeOnChanged.fold(context.some) { mm =>
                           if !mm.eqv(sessionOpt, now) then mm.whenDifferent(now, context.some)
                           else context.some
                         }
                         (next, ())
                       }
                     ) >> sessionCookie(id).map(response.response.putHeaders(_))
                   )
                 case (Some(id), None) =>
                   storage
                     .modify(
                       id,
                       { now =>
                         val next: Option[A] = mergeOnChanged.fold(Option.empty[A]) { mm =>
                           if !mm.eqv(sessionOpt, now) then mm.whenDifferent(now, Option.empty)
                           else None
                         }
                         (next, ())
                       }
                     )
                     .as(response.response.putHeaders(deleteCookie))
               )
      yield out
    }

  /** Methods for building middleware by reading necessary settings from Conf files.
    *
    * @param storage
    *   Session Storage
    * @param mergeOnChanged
    *   Model for context merging control
    * @param routes
    *   Route information using SessionStorage
    * @tparam F
    *   the effect type.
    * @tparam A
    *   Value managed by Session
    * @return
    *   A HttpRoutes
    */
  def fromConfig[F[_]: Monad: Clock, A](
    storage:        SessionStorage[F, A],
    mergeOnChanged: Option[MergeManagement[Option[A]]] = Option.empty[MergeManagement[Option[A]]]
  )(routes:         SessionRoutes[Option[A], F]): HttpRoutes[F] =
    val deleteCookie = generateDeleteCookie(
      sessionIdentifier,
      sessionDomain,
      sessionPath,
      sessionSameSite,
      sessionSecure,
      sessionHttpOnly
    )
    val expiration: ExpirationManagement[F] = sessionExpirationType match
      case "Static" => ExpirationManagement.Static(sessionExpirationMaxAge, sessionExpirationExpires)
      case "Dynamic" =>
        ExpirationManagement.Dynamic[F](httpDate =>
          Monad[F].pure(ExpirationManagement.Static(httpDate.epochSecond.some, httpDate.some))
        )
      case unknown =>
        throw new IllegalArgumentException(
          s"$unknown did not match any of the ExpirationManagement. The value of ExpirationManagement must be Static, or Dynamic."
        )

    def sessionCookie(id: SessionIdentifier): F[`Set-Cookie`] =
      expiration match
        case ExpirationManagement.Static(maxAge, expires) =>
          `Set-Cookie`(
            ResponseCookie(
              name     = sessionIdentifier,
              content  = id.value,
              expires  = expires,
              maxAge   = maxAge,
              domain   = sessionDomain,
              path     = sessionPath,
              sameSite = sessionSameSite.some,
              secure   = sessionSecure,
              httpOnly = sessionHttpOnly
            )
          ).pure[F]
        case e @ ExpirationManagement.Dynamic(fromNow) =>
          HttpDate.current[F](Functor[F], e.clock).flatMap { now =>
            fromNow(now).map {
              case ExpirationManagement.Static(maxAge, expires) =>
                `Set-Cookie`(
                  ResponseCookie(
                    name     = sessionIdentifier,
                    content  = id.value,
                    expires  = expires,
                    maxAge   = maxAge,
                    domain   = sessionDomain,
                    path     = sessionPath,
                    sameSite = sessionSameSite.some,
                    secure   = sessionSecure,
                    httpOnly = sessionHttpOnly
                  )
                )
            }
          }

    Kleisli { (request: Request[F]) =>
      val sessionId = SessionIdentifier.extract(request, sessionIdentifier)
      val session   = sessionId.flatTraverse(id => storage.get(id))

      for
        sessionOpt <- OptionT.liftF(session)
        response   <- routes(ContextRequest(sessionOpt, request))
        out <- OptionT.liftF((sessionId, response.context) match
                 case (None, None) => response.response.pure[F]
                 case (Some(id), Some(context)) =>
                   storage.modify(
                     id,
                     { now =>
                       val next: Option[A] = mergeOnChanged.fold(context.some) { mm =>
                         if !mm.eqv(sessionOpt, now) then mm.whenDifferent(now, context.some)
                         else context.some
                       }
                       (next, ())
                     }
                   ) >> sessionCookie(id).map(response.response.putHeaders(_))
                 case (None, Some(context)) =>
                   storage.sessionId.flatMap(id =>
                     storage.modify(
                       id,
                       { now =>
                         val next: Option[A] = mergeOnChanged.fold(context.some) { mm =>
                           if !mm.eqv(sessionOpt, now) then mm.whenDifferent(now, context.some)
                           else context.some
                         }
                         (next, ())
                       }
                     ) >> sessionCookie(id).map(response.response.putHeaders(_))
                   )
                 case (Some(id), None) =>
                   storage
                     .modify(
                       id,
                       { now =>
                         val next: Option[A] = mergeOnChanged.fold(Option.empty[A]) { mm =>
                           if !mm.eqv(sessionOpt, now) then mm.whenDifferent(now, Option.empty)
                           else None
                         }
                         (next, ())
                       }
                     )
                     .as(response.response.putHeaders(deleteCookie))
               )
      yield out
    }

  /** Method to generate cookie to remove Session information from cookie.
    *
    * @param name
    *   Cookie Key
    * @param domain
    *   Domain information to be set in cookies
    * @param path
    *   Path information to be set in cookies
    * @param sameSite
    *   SameSite information to be set in cookies
    * @param secure
    *   Secure information to be set in cookies
    * @param httpOnly
    *   HttpOnly information to be set in cookies
    * @return
    *   A Set-Cookie
    */
  private def generateDeleteCookie(
    name:     String,
    domain:   Option[String],
    path:     Option[String],
    sameSite: SameSite,
    secure:   Boolean,
    httpOnly: Boolean
  ): `Set-Cookie` =
    `Set-Cookie`(
      ResponseCookie(
        name     = name,
        content  = "deleted",
        expires  = Some(HttpDate.Epoch),
        maxAge   = Some(-1L),
        domain   = domain,
        path     = path,
        sameSite = sameSite.some,
        secure   = secure,
        httpOnly = httpOnly
      )
    )

  /** Http Servers allow concurrent requests. You may wish to specify how merges are managed if the context has been
    * concurrently modified while your service is holding some initial context.
    *
    * @tparam A
    *   Value managed by Session
    */
  trait MergeManagement[A]:

    /** Intended as equivalent to Eq.eqv in cats if present */
    def eqv(a1: A, a2: A): Boolean

    /** How to resolve conflicts when a difference from the initial and present state has occurred. */
    def whenDifferent(changedValue: A, valueContextWishesToSet: A): A

  object MergeManagement:
    def instance[A](areEqual: (A, A) => Boolean, conflictResolution: (A, A) => A): MergeManagement[A] =
      new MergeManagement[A]:
        def eqv(a1: A, a2: A): Boolean = areEqual(a1, a2)

        def whenDifferent(changedValue: A, valueContextWishesToSet: A): A =
          conflictResolution(changedValue, valueContextWishesToSet)

  /** ExpirationManagement is how you can control the expiration of your Session Cookies. Static is fairly straight
    * forward. Static(None, None) means your session is ephemeral and will be removed when the browser closes
    *
    * Max Age Should Be Preferred in all cases as the Expires specification is only in terms of client anyway so static
    * MaxAge is effective, but may not be supported by all clients. If it is relative to the current time like MaxAge
    * will likely need to use Dynamic rather than a Static to render an Expires, and can be leveraged for that.
    */
  sealed trait ExpirationManagement[F[_]]
  object ExpirationManagement:
    case class Static[F[_]](maxAge: Option[Long], expires: Option[HttpDate]) extends ExpirationManagement[F]

    case class Dynamic[F[_]](fromNow: HttpDate => F[Static[F]])(using val clock: Clock[F])
      extends ExpirationManagement[F]
