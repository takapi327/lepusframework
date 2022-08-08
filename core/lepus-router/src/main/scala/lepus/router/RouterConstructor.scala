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

  /** Alias of RequestMethod. */
  protected final val GET     = Method.Get
  protected final val HEAD    = Method.Head
  protected final val POST    = Method.Post
  protected final val PUT     = Method.Put
  protected final val DELETE  = Method.Delete
  protected final val OPTIONS = Method.Options
  protected final val PATCH   = Method.Patch
  protected final val CONNECT = Method.Connect
  protected final val TRACE   = Method.Trace

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
