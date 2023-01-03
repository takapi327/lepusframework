/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app.session

import org.http4s.{ SameSite, HttpDate }

import org.specs2.mutable.Specification

object DefaultSessionConfigReaderTest extends Specification, SessionConfigReader:

  "Testing the DefaultSessionConfigReader" should {
    "Identifier value retrieved with the default key matches the specified one." in {
      sessionIdentifier == "LEPUS_SESSION_TEST"
    }

    "Http Only value retrieved with the default key matches the specified one." in {
      !sessionHttpOnly
    }

    "Secure value retrieved with the default key matches the specified one." in {
      !sessionSecure
    }

    "Domain value retrieved with the default key matches the specified one." in {
      sessionDomain.contains("http://lepus.com")
    }

    "Path value retrieved with the default key matches the specified one." in {
      sessionPath.contains("session")
    }

    "SameSite value retrieved with the default key matches the specified one." in {
      sessionSameSite == SameSite.None
    }

    "Expiration Type value retrieved with the default key matches the specified one." in {
      sessionExpirationType == "Static"
    }

    "Expiration Max Age value retrieved with the default key matches the specified one." in {
      sessionExpirationMaxAge.contains(360)
    }

    "Expiration Expires value retrieved with the default key matches the specified one." in {
      sessionExpirationExpires.contains(HttpDate.unsafeFromEpochSecond(12345678))
    }
  }

object CustomSessionConfigReaderTest extends Specification, SessionConfigReader:
  override val SESSION: String = "lepus.custom.session"

  "Testing the CustomSessionConfigReader" should {
    "Identifier value retrieved with the custom key matches the specified one." in {
      sessionIdentifier == "LEPUS_SESSION_CUSTOM_TEST"
    }

    "Http Only value retrieved with the custom key matches the specified one." in {
      sessionHttpOnly
    }

    "Secure value retrieved with the custom key matches the specified one." in {
      sessionSecure
    }

    "Domain value retrieved with the custom key matches the specified one." in {
      sessionDomain.contains("http://lepus.com")
    }

    "Path value retrieved with the custom key matches the specified one." in {
      sessionPath.contains("session/custom")
    }

    "SameSite value retrieved with the custom key matches the specified one." in {
      sessionSameSite == SameSite.Strict
    }

    "Expiration Type value retrieved with the custom key matches the specified one." in {
      sessionExpirationType == "Dynamic"
    }

    "Expiration Max Age value retrieved with the custom key matches the specified one." in {
      sessionExpirationMaxAge.contains(60)
    }

    "Expiration Expires value retrieved with the custom key matches the specified one." in {
      sessionExpirationExpires.contains(HttpDate.unsafeFromEpochSecond(1234))
    }
  }

object FailureSessionConfigReaderTest extends Specification, SessionConfigReader:
  override val SESSION: String = "lepus.failure.session"

  "Testing the FailureSessionConfigReader" should {
    "An IllegalArgumentException exception is raised if SameSite is not a value in the specified format." in {
      sessionSameSite must throwAn[IllegalArgumentException]
    }

    "An IllegalArgumentException exception is raised if Expiration Type is not a value in the specified format." in {
      sessionExpirationType must throwAn[IllegalArgumentException]
    }

    "An IllegalArgumentException exception is raised if Expiration Expires is not a value in the specified format." in {
      sessionExpirationExpires must throwAn[IllegalArgumentException]
    }
  }
