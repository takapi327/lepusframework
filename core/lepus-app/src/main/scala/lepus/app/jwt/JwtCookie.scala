/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app.jwt

import scala.jdk.CollectionConverters.*

import cats.data.NonEmptyMap
import cats.syntax.all.*

import io.jsonwebtoken.Jwts

import org.http4s.*

private[lepus] final case class JwtCookie(
  name:    String,
  content: String,
  expires: Option[HttpDate] = None,
  maxAge:  Option[Long] = None,
  domain:  Option[String] = None,
  path:     Option[String] = None,
  sameSite: Option[SameSite] = None,
  secure: Boolean = false,
  httpOnly: Boolean = false,
  extension: Option[String] = None,
):

  private[lepus] def toResponseCookie: ResponseCookie =
    ResponseCookie(name, content, expires, maxAge, domain, path, sameSite, secure, httpOnly, extension)

  def withExpires(expires: HttpDate): JwtCookie =
    copy(expires = Some(expires))

  def withMaxAge(maxAge: Long): JwtCookie =
    copy(maxAge = Some(maxAge))

  def withDomain(domain: String): JwtCookie =
    copy(domain = Some(domain))

  def withPath(path: String): JwtCookie =
    copy(path = Some(path))

  def withSecure(secure: Boolean): JwtCookie =
    copy(secure = secure)

  def withHttpOnly(httpOnly: Boolean): JwtCookie =
    copy(httpOnly = httpOnly)

  def withSameSite(sameSite: SameSite): JwtCookie =
    copy(sameSite = Some(sameSite))

  def withExtension(extension: String): JwtCookie =
    copy(extension = Some(extension))

  def addExtension(extensionString: String): JwtCookie =
    copy(extension = extension.fold(Some(extensionString))(old => Some(old + "; " + extensionString)))

/**
 * Class for constructing cookies for Jwt.
 *
 * @param jwt
 *   Setup to build Jwt.
 */
class JwtCookieBuilder(jwt: JwtSettings):

  def apply(key: String, content: NonEmptyMap[String, String]): JwtCookie =
    this.build(key, content)

  def build(
    key: String,
    content: NonEmptyMap[String, String],
    expires: Option[HttpDate] = None,
    maxAge: Option[Long] = None,
    domain: Option[String] = None,
    path: Option[String] = None,
    sameSite: Option[SameSite] = None,
    secure: Boolean = false,
    httpOnly: Boolean = false,
    extension: Option[String] = None,
  ): JwtCookie =
    JwtCookie(
      name = key,
      content = jwt.formatter.format(NonEmptyMap.one(jwt.configReader.claimKey, Jwts.claims(content.toNel.toList.toMap.asJava))),
      expires = expires,
      maxAge = maxAge,
      domain = domain,
      path = path,
      sameSite = sameSite,
      secure = secure,
      httpOnly = httpOnly,
      extension = extension
    )

  def fromConfig(a: (String, String), as: (String, String)*): JwtCookie =
    this.fromConfig(NonEmptyMap.of(a, as: _*))

  def fromConfig(content: NonEmptyMap[String, String]): JwtCookie =
    this.build(
      key       = jwt.configReader.cookieKey,
      content   = content,
      expires   = jwt.configReader.expires,
      maxAge    = jwt.configReader.maxAge,
      domain    = jwt.configReader.domain,
      path      = jwt.configReader.path,
      sameSite  = jwt.configReader.sameSite.some,
      secure    = jwt.configReader.secure,
      httpOnly  = jwt.configReader.httpOnly,
      extension = jwt.configReader.extension
    )
