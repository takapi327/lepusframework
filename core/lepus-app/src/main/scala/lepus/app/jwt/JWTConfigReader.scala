/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app.jwt

import scala.concurrent.duration.*

import io.jsonwebtoken.SignatureAlgorithm

import lepus.core.util.Configuration

/**
 * Object to read the information needed to configure the JWT from the conf file.
 */
private[lepus] trait JWTConfigReader:

  private val config: Configuration = Configuration.load()

  /** Key to retrieve the value of jwt. If you want to change the key, you need to overwrite it at the inherited
   * location.
   */
  val JWT: String = "lepus.jwt"

  private final lazy val JWT_SIGNATURE_ALGORITHM: String = JWT + ".signature_algorithm"
  private final lazy val JWT_EXPIRES_AFTER:       String = JWT + ".expires_after"
  private final lazy val JWT_CLOCK_SKEW:          String = JWT + ".clock_skew"
  private final lazy val JWT_CLAIM_KEY:           String = JWT + ".claim_key"

  protected lazy val signatureAlgorithm: SignatureAlgorithm =
    config.get[Option[String]](JWT_SIGNATURE_ALGORITHM).fold(
      SignatureAlgorithm.HS256
    )(SignatureAlgorithm.forName)

  protected lazy val expiresAfter: Option[FiniteDuration] =
    config.get[Option[FiniteDuration]](JWT_EXPIRES_AFTER)

  protected lazy val clockSkew: FiniteDuration =
    config.get[Option[FiniteDuration]](JWT_CLOCK_SKEW).getOrElse(30.seconds)

  protected lazy val claimKey: String =
    config.get[Option[String]](JWT_CLAIM_KEY).getOrElse("data")
