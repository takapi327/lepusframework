/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import language.experimental.macros

import cats.Semigroup
import cats.syntax.semigroupk.*

import cats.effect.IO

import org.http4s.{ HttpRoutes as Http4sRoutes, Request, Response, Method }

import lepus.router.RouterConstructor
import lepus.router.http.RequestEndpoint

package object router extends LepusRouter:

  type Http[T] = PartialFunction[Method, T]

  type HttpRoutes[F[_]] = Http[F[Response[F]]]

  given Semigroup[Http4sRoutes[IO]] = _ combineK _

  type Routing[F[_]]     = (RequestEndpoint.Endpoint[?], RouterConstructor[F, ?])
  type Requestable[F[_]] = [T] =>> T ?=> Request[F] ?=> HttpRoutes[F]
