/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app.jwt

import java.time.Clock as JClock
import java.util.Date

import javax.crypto.SecretKey
import javax.inject.Inject

import scala.jdk.CollectionConverters.*

import cats.data.NonEmptyMap

import com.fasterxml.jackson.databind.ObjectMapper

import io.jsonwebtoken.{ Jwts, Clock, JwtParser, Jws, Claims }
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.jackson.io.{ JacksonDeserializer, JacksonSerializer }

trait JwtFormatter:

  /**
   * Parses encoded JWT against configuration, returns all JWT claims.
   *
   * @param encodedString
   * the signed and encoded JWT.
   * @return
   * the map of claims
   */
  def parse(encodedString: String): Map[String, AnyRef]

  /**
   * Formats the input claims to a JWT string, and adds extra date related claims.
   *
   * @param claims
   * all the claims to be added to JWT.
   * @return
   * the signed, encoded JWT with extra date related claims
   */
  def format(claims: NonEmptyMap[String, AnyRef]): String

case class DefaultJwtFormatter @Inject()(
  configReader: JwtConfigReader
) extends JwtFormatter:

  private val objectMapper: ObjectMapper = new ObjectMapper()

  private val jwtClock: Clock = () => Date.from(JClock.systemUTC().instant())

  private val secretKey: SecretKey = Keys.secretKeyFor(configReader.signatureAlgorithm)

  private val jwtParser: JwtParser = Jwts.parserBuilder()
    .setClock(jwtClock)
    .setSigningKey(secretKey)
    .setAllowedClockSkewSeconds(configReader.clockSkew.toSeconds)
    .deserializeJsonWith(new JacksonDeserializer(objectMapper))
    .build()

  /**
   * Parses encoded JWT against configuration, returns all JWT claims.
   *
   * @param encodedString
   * the signed and encoded JWT.
   * @return
   * the map of claims
   */
  override def parse(encodedString: String): Map[String, AnyRef] =
    val jws: Jws[Claims] = jwtParser.parseClaimsJws(encodedString)

    val headerAlgorithm = jws.getHeader.getAlgorithm
    if headerAlgorithm != configReader.signatureAlgorithm.getValue then
      val id = jws.getBody.getId
      val msg = s"Invalid header algorithm $headerAlgorithm in JWT $id"
      throw new IllegalStateException(msg)

    jws.getBody.asScala.toMap

  /**
   * Formats the input claims to a JWT string, and adds extra date related claims.
   *
   * @param claims
   * all the claims to be added to JWT.
   * @return
   * the signed, encoded JWT with extra date related claims
   */
  override def format(claims: NonEmptyMap[String, AnyRef]): String =
    val builder = Jwts.builder().serializeToJsonWith(new JacksonSerializer(objectMapper))
    val now = jwtClock.now()

    configReader.expiresAfter.map(duration =>
      val expirationDate = new Date(now.getTime + duration.toMillis)
      builder.setExpiration(expirationDate)
    )

    builder
      .signWith(secretKey, configReader.signatureAlgorithm)
      .setClaims(claims.toNel.toList.toMap.asJava)
      .setNotBefore(now)
      .setIssuedAt(now)
      .base64UrlEncodeWith(Encoders.BASE64URL)
      .compact()
