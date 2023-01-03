/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app.session

import cats.{ Applicative, Monad }
import cats.data.{ OptionT, Kleisli }
import cats.syntax.all.*

import org.http4s.*

/** Type alias for Routes that receives a ContextRequest and returns a ContextResponse. */
type SessionRoutes[F[_], T] = Kleisli[[A] =>> OptionT[F, A], ContextRequest[F, T], ContextResponse[F, T]]

/**
 * An object for using Session.
 */
object SessionRoutes:

  /** Methods for generating Routes that receive a ContextRequest and return a ContextResponse. */
  def of[F[_]: Monad, A](pf: PartialFunction[ContextRequest[F, A], F[ContextResponse[F, A]]]): SessionRoutes[F, A] =
    Kleisli(req => OptionT(Applicative[F].unit >> pf.lift(req).sequence))
