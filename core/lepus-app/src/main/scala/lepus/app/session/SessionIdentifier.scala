/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.app.session

import java.util.Base64

import cats.Functor
import cats.syntax.all.*

import cats.effect.std.Random

import org.http4s.Request

/** Session identifier
  *
  * copied from http4s-session:
  * https://github.com/http4s/http4s-session/blob/main/core/src/main/scala/org/http4s/session/SessionIdentifier.scala
  *
  * @param value
  *   String that is the identifier of the Session
  */
private[lepus] case class SessionIdentifier(value: String)

private[lepus] object SessionIdentifier:

  private val base64 = Base64.getEncoder

  /** Generator for a SessionIdentifier
    *
    * @param random
    *   Random is the ability to get random information, each time getting a different result.
    * @param numBytes
    *   Minimum recommended by OWASP is 16 bytes if you have 64 bits of entropy
    * @tparam F
    *   the effect type.
    * @return
    *   A Session Identifier
    */
  def create[F[_]: Functor](random: Random[F], numBytes: Int): F[SessionIdentifier] =
    random.nextBytes(numBytes).map(v => SessionIdentifier(base64.encodeToString(v)))

  /** Convenience Method From Extracting a Session from a Request
    *
    * @param request
    *   Request to extract from
    * @param sessionIdentifierName
    *   The Name Of The Session Identifier - A.k.a Which cookie its in
    * @tparam F
    *   the effect type.
    * @return
    *   A Session Identifier
    */
  def extract[F[_]](request: Request[F], sessionIdentifierName: String): Option[SessionIdentifier] =
    request.cookies.find(_.name === sessionIdentifierName).map(v => SessionIdentifier(v.content))
