/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus

import cats.Semigroup
import cats.syntax.semigroupk._

import cats.effect.IO

import org.http4s.HttpRoutes

import lepus.router.http.RequestMethod
import lepus.router.model.{ ServerRequest, ServerResponse }

package object router extends LepusRouter with ExtensionMethods {

  type Routes[F[_], T] = PartialFunction[RequestMethod, ServerRequest[F, T] => F[ServerResponse]]

  implicit val routesSemigroup: Semigroup[HttpRoutes[IO]] = _ combineK _
}
