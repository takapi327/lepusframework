/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import cats.Semigroup
import cats.syntax.semigroupk._

import cats.effect.IO

import org.http4s.{ HttpRoutes => Http4sRoutes }

import lepus.router.http.RequestMethod
import lepus.router.model.{ ServerRequest, ServerResponse }

package object router extends LepusRouter with ExtensionMethods {

  type Http[T] = PartialFunction[RequestMethod, T]

  type HttpResponse[T]     = Http[T]
  type HttpRequest[T]      = Http[T]
  type HttpRoutes[F[_], T] = Http[ServerRequest[F, T] => F[ServerResponse]]

  implicit val routesSemigroup: Semigroup[Http4sRoutes[IO]] = _ combineK _
}
