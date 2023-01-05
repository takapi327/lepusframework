/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app.jwt

import scala.jdk.CollectionConverters.*

import org.typelevel.ci.CIString

import cats.syntax.all.*

import org.typelevel.vault.Key

import org.http4s.*
import org.http4s.headers.*

/**
 * Helper to build Jwt in your application.
 *
 * example:
 * {{{
 *   class JwtService @Inject()(
 *     jwt: JwtSettings
 *   ) extends JwtHelper(jwt):
 *
 *     val cookie: JwtCookie = JwtCookie.fromConfig("name" -> "Lepus")
 *
 *     ...
 * }}}
 */
trait JwtHelper(jwt: JwtSettings):

  private[lepus] final val JwtCookie: JwtCookieBuilder = new JwtCookieBuilder(jwt)

  extension[F[_] : cats.Functor] (request: Request[F])
    def jwtCookies(key: String): Map[String, String] =
      request.cookies.find(_.name == key).fold(Map.empty)(cookie =>
        val parsed = jwt.formatter.parse(cookie.content)
        val claimMap = parsed.get(jwt.configReader.claimKey).map(_.asInstanceOf[java.util.Map[String, AnyRef]].asScala)
        claimMap.fold(Map.empty[String, String])(_.view.mapValues(_.toString).toMap)
      )

    def jwtCookies: Map[String, String] =
      request.jwtCookies(jwt.configReader.cookieKey)

  given Conversion[JwtCookie, ResponseCookie] = _.toResponseCookie

  extension[F[_] : cats.Functor] (response: Response[F])
    def addJwtCookie(cookie: JwtCookie): Response[F] =
      response.addCookie(cookie.toResponseCookie)

    def addJwtCookies(a: (String, String), as: (String, String)*): Response[F] =
      response.addCookie(JwtCookie.fromConfig(a, as: _*))

  extension[F[_] : cats.Functor] (response: F[Response[F]])
    def addJwtCookie(cookie: JwtCookie): F[Response[F]] =
      response.map(_.addJwtCookie(cookie))

    def addJwtCookies(a: (String, String), as: (String, String)*): F[Response[F]] =
      response.map(_.addJwtCookies(a, as: _*))
