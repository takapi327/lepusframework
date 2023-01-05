/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.app.jwt

import scala.concurrent.duration.*

import org.specs2.mutable.Specification

import com.typesafe.config.ConfigException

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.SignatureException

object DefaultJwtConfigReaderTest extends Specification, JwtConfigReader:

  "Testing the JWTConfigReader default key" should {
    "Signature Algorithm value retrieved with the default key matches the specified one." in {
      signatureAlgorithm === SignatureAlgorithm.HS256
    }

    "Expires After value retrieved with the default key matches the specified one." in {
      expiresAfter.contains(30.seconds)
    }

    "Clock Skew value retrieved with the default key matches the specified one." in {
      clockSkew === 30.seconds
    }

    "Data Claim value retrieved with the default key matches the specified one." in {
      claimKey === "data"
    }
  }

object CustomJwtConfigReaderTest extends Specification, JwtConfigReader:

  override val JWT: String = "lepus.custom.jwt"

  "Testing the JWTConfigReader custom key" should {
    "Signature Algorithm value retrieved with the custom key matches the specified one." in {
      signatureAlgorithm === SignatureAlgorithm.RS256
    }

    "Expires After value retrieved with the custom key matches the specified one." in {
      expiresAfter.contains(60.seconds)
    }

    "Clock Skew value retrieved with the custom key matches the specified one." in {
      clockSkew === 60.seconds
    }

    "Data Claim value retrieved with the custom key matches the specified one." in {
      claimKey === "custom"
    }
  }

object FailureJwtConfigReaderTest extends Specification, JwtConfigReader:

  override val JWT: String = "lepus.failure.jwt"

  "Testing the JWTConfigReader failure key" should {
    "An SignatureException exception is raised if Signature Algorithm is not a value in the specified format." in {
      signatureAlgorithm must throwA[SignatureException]
    }

    "An ConfigException exception is raised if Expires After is not a value in the specified format." in {
      expiresAfter must throwA[ConfigException]
    }

    "An ConfigException exception is raised if Clock Skew is not a value in the specified format." in {
      clockSkew must throwA[ConfigException]
    }
  }
