/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import cats.effect.{ Async, Sync }

import org.http4s.HttpRoutes as Http4sRoutes

import lepus.router.http.{ Request, Response, Header, Method }
import lepus.router.model.ServerResponse

/** A model that contains one routing information.
  *
  * For example:
  * {{{
  *   // http:localhost:5555/hello/world/lepus
  *   object HelloRoute extends RouterConstructor[IO]:
  *     def routes: Routes[IO, Param] = {
  *       case GET => IO(ServerResponse.NoContent)
  *     }
  * }}}
  *
  * @tparam F
  *   the effect type.
  */
abstract class RouterConstructor[F[_]](using Async[F], Sync[F]):

  /** Alias of ResponseStatus. */
  protected final val status = Response.Status

  /** Alias of ResponseHeader. */
  protected final val header = Header

  /** Alias of ServerResponse. */
  protected final val response = ServerResponse

  def requestBodies: Http[Request.Body[?]] = PartialFunction.empty

  /** An array of responses returned by each method. */
  def responses: Http[List[Response[?]]] = PartialFunction.empty

  /** Corresponding logic for each method of this endpoint. */
  def routes: HttpRoutes[F]
