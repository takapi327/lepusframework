/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.app.session

import lepus.core.util.Configuration

import org.http4s.{ SameSite, HttpDate }

/** Object to read the information needed to configure the Session from the conf file.
  */
private[lepus] trait SessionConfigReader:

  private val config: Configuration = Configuration.load()

  /** Key to retrieve the value of session. If you want to change the key, you need to overwrite it at the inherited location. */
  val SESSION: String = "lepus.session"

  private final lazy val SESSION_IDENTIFIER:         String = SESSION + ".identifier"
  private final lazy val SESSION_HTTP_ONLY:          String = SESSION + ".http_only"
  private final lazy val SESSION_SECURE:             String = SESSION + ".secure"
  private final lazy val SESSION_DOMAIN:             String = SESSION + ".domain"
  private final lazy val SESSION_PATH:               String = SESSION + ".path"
  private final lazy val SESSION_SAME_SITE:          String = SESSION + ".same_site"
  private final lazy val SESSION_EXPIRATION_TYPE:    String = SESSION + ".expiration.type"
  private final lazy val SESSION_EXPIRATION_MAX_AGE: String = SESSION + ".expiration.max_age"
  private final lazy val SESSION_EXPIRATION_EXPIRES: String = SESSION + ".expiration.expires"

  /** Session identifier managed by cookies. Default is LEPUS_SESSION */
  protected lazy val sessionIdentifier: String =
    config.get[Option[String]](SESSION_IDENTIFIER).getOrElse("LEPUS_SESSION")

  /** HttpOnly managed by cookies. Default is true */
  protected lazy val sessionHttpOnly: Boolean = config.get[Option[Boolean]](SESSION_HTTP_ONLY).getOrElse(true)

  /** Secure managed by cookies. Default is true */
  protected lazy val sessionSecure: Boolean = config.get[Option[Boolean]](SESSION_SECURE).getOrElse(true)

  /** Domain managed by cookies. */
  protected lazy val sessionDomain: Option[String] = config.get[Option[String]](SESSION_DOMAIN)

  /** Path managed by cookies. */
  protected lazy val sessionPath: Option[String] = config.get[Option[String]](SESSION_PATH)

  /** SameSite managed by cookies. Default is Lax */
  protected lazy val sessionSameSite: SameSite = config
    .get[Option[String]](SESSION_SAME_SITE)
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

  /** Type of object that controls the cookie expiration date. Default is Static */
  protected lazy val sessionExpirationType: String =
    config.get[Option[String]](SESSION_EXPIRATION_TYPE)
      .fold("Static")(str => str match
        case s: "Static"  => s
        case s: "Dynamic" => s
        case unknown =>
          throw new IllegalArgumentException(
            s"$unknown did not match any of the Expiration Type. The value of Expiration Type must be Static, or Dynamic."
          )
      )

  /** MaxAge managed by cookies. */
  protected lazy val sessionExpirationMaxAge: Option[Long] = config.get[Option[Long]](SESSION_EXPIRATION_MAX_AGE)

  /** Expires managed by cookies. */
  protected lazy val sessionExpirationExpires: Option[HttpDate] =
    config
      .get[Option[Long]](SESSION_EXPIRATION_EXPIRES)
      .map(long =>
        HttpDate.fromEpochSecond(long) match
          case Right(value) => value
          case Left(value)  => throw new IllegalArgumentException(value.getMessage)
      )
