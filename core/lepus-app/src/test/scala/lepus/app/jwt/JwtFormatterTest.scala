/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app.jwt

import java.util.Date
import java.time.Clock

import scala.jdk.CollectionConverters.*

import org.specs2.mutable.Specification

import io.jsonwebtoken.Jwts

import cats.data.NonEmptyMap

object JwtFormatterTest extends Specification:

  val formatter: JwtFormatter = DefaultJwtFormatter(DefaultJwtConfigReader())

  "Testing the JwtFormatter" should {
    "If the Jwt-signed value is verified, it matches the specified value." in {
      val jws    = formatter.format(NonEmptyMap.of("id" -> "1", "name" -> "lepus"))
      val parsed = formatter.parse(jws)
      val now    = Date.from(Clock.systemUTC().instant()).toInstant.getEpochSecond
      parsed === Map("id" -> "1", "name" -> "lepus", "nbf" -> now, "iat" -> now)
    }
  }
