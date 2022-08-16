/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import org.http4s.HttpRoutes as Http4sRoutes

import lepus.router.http.{ Request, Response, Header, Method }
import lepus.router.model.{ Schema, ServerResponse }

/** A model that contains one routing information.
  *
  * For example:
  * {{{
  *   // http:localhost:5555/hello/world/lepus
  *   object HelloRoute extends RouterConstructor[IO, String]:
  *     def routes = {
  *       case GET => IO(ServerResponse.NoContent)
  *     }
  * }}}
  *
  * @tparam F
  *   the effect type.
  */
trait RouterConstructor[F[_], T]:

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
  def routes: Requestable[F][T]

object RouterConstructor:
  def of[F[_], T](
    requestable: Requestable[F][T]
  ): RouterConstructor[F, T] = new RouterConstructor[F, T]:
    override def routes: Requestable[F][T] = requestable
