/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.app.jwt

import org.typelevel.ci.*

import cats.data.{ NonEmptyMap, NonEmptyList }

import cats.effect.IO

import org.http4s.*

import org.specs2.mutable.Specification

val defaultJwtSettings = DefaultJwtSettings(DefaultJwtConfigReader(), DefaultJwtFormatter(DefaultJwtConfigReader()))

object JwtCookieTest extends Specification, JwtHelper(defaultJwtSettings):

  "Testing the JwtCookie" should {

    "Jwt prominent cookies are stored in the response header." in {
      val cookie   = JwtCookie.fromConfig("id" -> "1", "name" -> "lepus")
      val response = Response[IO](Status.Ok).addCookie(cookie)

      response.headers
        .get(CIString("Set-Cookie"))
        .contains(
          NonEmptyList.one(
            Header.Raw(
              CIString("Set-Cookie"),
              s"LEPUS_JWT_COOKIE=${ cookie.content }; Expires=Sat, 23 May 1970 21:21:18 GMT; Domain=http://lepus.com; Path=jwt; SameSite=None; Secure"
            )
          )
        )
    }

    "Jwt signed values retrieved via Request will match the specified value." in {
      val cookie  = JwtCookie.fromConfig("id" -> "1", "name" -> "lepus")
      val request = Request[IO]().addCookie(cookieKey, cookie.content)

      request.jwtCookies === Map("id" -> "1", "name" -> "lepus") and
        request.jwtCookies.get("id").contains("1") and
        request.jwtCookies.get("name").contains("lepus")
    }
  }
