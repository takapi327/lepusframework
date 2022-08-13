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
import lepus.router.http.{ Request, RequestEndpoint, Method }
import lepus.router.model.{ ServerRequest, ServerResponse }

package object router extends LepusRouter:

  type Http[T] = PartialFunction[Method, T]

  type HttpRoutes[F[_]] = Http[F[ServerResponse]]

  given Semigroup[Http4sRoutes[IO]] = _ combineK _

  type Routing[F[_]] = (RequestEndpoint.Endpoint, RouterConstructor[F, ?])
  type Requestable[F[_]] = [T] =>> T ?=> Request[F] ?=> HttpRoutes[F]

  /** Alias of RequestMethod. */
  final val GET     = Method.Get
  final val HEAD    = Method.Head
  final val POST    = Method.Post
  final val PUT     = Method.Put
  final val DELETE  = Method.Delete
  final val OPTIONS = Method.Options
  final val PATCH   = Method.Patch
  final val CONNECT = Method.Connect
  final val TRACE   = Method.Trace
