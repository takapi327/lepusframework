/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import language.experimental.macros

import cats.Semigroup
import cats.syntax.semigroupk.*

import cats.effect.IO

import org.http4s.HttpRoutes as Http4sRoutes

import lepus.router.RouterConstructor
import lepus.router.http.RequestEndpoint
import lepus.router.http.Method
import lepus.router.model.{ ServerRequest, ServerResponse }

package object router extends LepusRouter:

  type Http[T] = PartialFunction[Method, T]

  type HttpRoutes[F[_], T] = Http[ServerRequest[F, T] => F[ServerResponse]]

  given Semigroup[Http4sRoutes[IO]] = _ combineK _

  type Route[F[_]] = (RequestEndpoint.Endpoint, RouterConstructor[F, ?])
