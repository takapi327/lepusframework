/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.app.jwt

import javax.inject.Inject

import scala.concurrent.duration.*

import io.jsonwebtoken.SignatureAlgorithm

import org.http4s.{ SameSite, HttpDate }

import lepus.core.util.Configuration

/** Object to read the information needed to configure the JWT from the conf file.
  */
trait JwtConfigReader:

  private val config: Configuration = Configuration.load()

  /** Key to retrieve the value of jwt. If you want to change the key, you need to overwrite it at the inherited
    * location.
    */
  val JWT: String = "lepus.jwt"

  private final lazy val JWT_SIGNATURE_ALGORITHM: String = JWT + ".signature_algorithm"
  private final lazy val JWT_EXPIRES_AFTER:       String = JWT + ".expires_after"
  private final lazy val JWT_CLOCK_SKEW:          String = JWT + ".clock_skew"
  private final lazy val JWT_CLAIM_KEY:           String = JWT + ".claim_key"
  private final lazy val JWT_COOKIE_KEY:          String = JWT + ".cookie.key"
  private final lazy val JWT_COOKIE_MAX_AGE:      String = JWT + ".cookie.max_age"
  private final lazy val JWT_COOKIE_HTTP_ONLY:    String = JWT + ".cookie.http_only"
  private final lazy val JWT_COOKIE_SECURE:       String = JWT + ".cookie.secure"
  private final lazy val JWT_COOKIE_DOMAIN:       String = JWT + ".cookie.domain"
  private final lazy val JWT_COOKIE_PATH:         String = JWT + ".cookie.path"
  private final lazy val JWT_COOKIE_SAME_SITE:    String = JWT + ".cookie.same_site"
  private final lazy val JWT_COOKIE_EXPIRES:      String = JWT + ".cookie.expires"
  private final lazy val JWT_COOKIE_EXTENSION:    String = JWT + ".cookie.extension"

  lazy val signatureAlgorithm: SignatureAlgorithm =
    config
      .get[Option[String]](JWT_SIGNATURE_ALGORITHM)
      .fold(SignatureAlgorithm.HS256)(SignatureAlgorithm.forName)

  lazy val expiresAfter: Option[FiniteDuration] =
    config.get[Option[FiniteDuration]](JWT_EXPIRES_AFTER)

  lazy val clockSkew: FiniteDuration =
    config.get[Option[FiniteDuration]](JWT_CLOCK_SKEW).getOrElse(30.seconds)

  lazy val claimKey: String =
    config.get[Option[String]](JWT_CLAIM_KEY).getOrElse("data")

  /** Cookie key managed by cookies. Default is LEPUS_JWT_COOKIE */
  lazy val cookieKey: String =
    config.get[Option[String]](JWT_COOKIE_KEY).getOrElse("LEPUS_JWT_COOKIE")

  /** Expires managed by cookies. */
  lazy val expires: Option[HttpDate] =
    config
      .get[Option[Long]](JWT_COOKIE_EXPIRES)
      .map(long =>
        HttpDate.fromEpochSecond(long) match
          case Right(value) => value
          case Left(value)  => throw new IllegalArgumentException(value.getMessage)
      )

  /** Cookie key managed by cookies. Default is LEPUS_JWT_COOKIE */
  lazy val maxAge: Option[Long] =
    config.get[Option[Long]](JWT_COOKIE_MAX_AGE)

  /** HttpOnly managed by cookies. Default is true */
  lazy val httpOnly: Boolean = config.get[Option[Boolean]](JWT_COOKIE_HTTP_ONLY).getOrElse(true)

  /** Secure managed by cookies. Default is true */
  lazy val secure: Boolean = config.get[Option[Boolean]](JWT_COOKIE_SECURE).getOrElse(true)

  /** Domain managed by cookies. */
  lazy val domain: Option[String] = config.get[Option[String]](JWT_COOKIE_DOMAIN)

  /** Path managed by cookies. */
  lazy val path: Option[String] = config.get[Option[String]](JWT_COOKIE_PATH)

  /** SameSite managed by cookies. Default is Lax */
  lazy val sameSite: SameSite = config
    .get[Option[String]](JWT_COOKIE_SAME_SITE)
    .fold(SameSite.Lax)(str =>
      str match
        case "Lax"    => SameSite.Lax
        case "Strict" => SameSite.Strict
        case "None"   => SameSite.None
        case unknown =>
          throw new IllegalArgumentException(
            s"$unknown did not match any of the SameSites. The value of SameSite must be Lax, Strict, or None."
          )
    )

  /** Extension managed by cookies. */
  lazy val extension: Option[String] =
    config.get[Option[String]](JWT_COOKIE_EXTENSION)

case class DefaultJwtConfigReader() extends JwtConfigReader
