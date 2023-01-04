/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app

import org.typelevel.ci.CIString

import cats.syntax.all.*

import org.typelevel.vault.Key

import org.http4s.*
import org.http4s.headers.*

object syntax:

  /**
   * copied from http4s-session:
   * https://github.com/http4s/http4s-session/blob/main/core/src/main/scala/org/http4s/session/syntax.scala
   */
  extension[F[_]: cats.Functor] (response: Response[F])
    def withContext[A](a: A): ContextResponse[F, A] = ContextResponse(a, response)

  /**
   * Aliases for methods included in the Response that can be accessed even when enclosed in an Effect Type.
   */
  extension[F[_]: cats.Functor] (response: F[Response[F]])

    def addCookie(cookie: ResponseCookie): F[Response[F]] =
      response.map(_.addCookie(cookie))

    def addCookie(name: String, content: String, expires: Option[HttpDate] = None): F[Response[F]] =
      response.map(_.addCookie(name, content, expires))

    def removeCookie(cookie: ResponseCookie): F[Response[F]] =
      response.map(_.removeCookie(cookie))

    def removeCookie(name: String): F[Response[F]] =
      response.map(_.removeCookie(name))

    def withHttpVersion(httpVersion: HttpVersion): F[Response[F]] =
      response.map(_.withHttpVersion(httpVersion))

    def withHeaders(headers: Headers): F[Response[F]] =
      response.map(_.withHeaders(headers))

    def withHeaders(headers: Header.ToRaw*): F[Response[F]] =
      response.map(_.withHeaders(headers: _*))

    def withEntity[T](b: T)(using EntityEncoder[F, T]): F[Response[F]] =
      response.map(_.withEntity(b))

    def withBodyStream(body: EntityBody[F]): F[Response[F]] =
      response.map(_.withBodyStream(body))

    def withEmptyBody: F[Response[F]] =
      response.map(_.withEmptyBody)

    def transformHeaders(f: Headers => Headers): F[Response[F]] =
      response.map(_.transformHeaders(f))

    def filterHeaders(f: Header.Raw => Boolean): F[Response[F]] =
      response.map(_.filterHeaders(f))

    def removeHeader(key: CIString): F[Response[F]] =
      response.map(_.removeHeader(key))

    def removeHeader[A](using Header[A, ?]): F[Response[F]] =
      response.map(_.removeHeader)

    def putHeaders(headers: Header.ToRaw*): F[Response[F]] =
      response.map(_.putHeaders(headers: _*))

    def addHeader[H: [T] =>> Header[T, Header.Recurring]](h: H): F[Response[F]] =
      response.map(_.addHeader(h))

    def withTrailerHeaders(trailerHeaders: F[Headers]): F[Response[F]] =
      response.map(_.withTrailerHeaders(trailerHeaders))

    def withoutTrailerHeaders: F[Response[F]] =
      response.map(_.withoutTrailerHeaders)

    def withContentType(contentType: `Content-Type`): F[Response[F]] =
      response.map(_.withContentType(contentType))

    def withoutContentType: F[Response[F]] =
      response.map(_.withoutContentType)

    def withContentTypeOption(contentTypeO: Option[`Content-Type`]): F[Response[F]] =
      response.map(_.withContentTypeOption(contentTypeO))

    def withAttribute[A](key: Key[A], value: A): F[Response[F]] =
      response.map(_.withAttribute(key, value))

    def withoutAttribute(key: Key[?]): F[Response[F]] =
      response.map(_.withoutAttribute(key))

    /**
     * copied from http4s-session:
     * https://github.com/http4s/http4s-session/blob/main/core/src/main/scala/org/http4s/session/syntax.scala
     */
    def withContext[A](a: A): F[ContextResponse[F, A]] =
      response.map(_.withContext(a))
