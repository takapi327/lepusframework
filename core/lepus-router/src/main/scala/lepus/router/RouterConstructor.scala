/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router

import cats.effect.{ Async, Sync }

import org.http4s.HttpRoutes

import lepus.router.http.{ RequestEndpoint, RequestMethod }

/**
 *
 * For example:
 * {{{
 *   // http:localhost:5555/hello/world/lepus
 *   object HelloEndpoint extends RouterConstructor[IO] {
 *     override type Param = (String, Long)
 *     override def endpoint: RequestEndpoint[_] =
 *       "hello" / bindPath[String]("world") / bindPath[String]("name")
 *
 *     override def summary = Some("Hello World!")
 *
 *     override def routes: Routes[IO, Param] = {
 *       case GET => _ => IO(ServerResponse.NoContent)
 *     }
 *   }
 * }}}
 *
 * @tparam F the effect type.
 */
abstract class RouterConstructor[F[_]](implicit asyncF: Async[F], syncF: Sync[F]) {

  /** The combined type of the Http request path and query parameters. */
  type Param

  /** Alias of RequestMethod. */
  protected final val GET     = RequestMethod.Get
  protected final val HEAD    = RequestMethod.Head
  protected final val POST    = RequestMethod.Post
  protected final val PUT     = RequestMethod.Put
  protected final val DELETE  = RequestMethod.Delete
  protected final val OPTIONS = RequestMethod.Options
  protected final val PATCH   = RequestMethod.Patch
  protected final val CONNECT = RequestMethod.Connect
  protected final val TRACE   = RequestMethod.Trace

  /** Alias of ServerResponse. */
  protected final val ServerResponse = lepus.router.model.ServerResponse

  /** List of methods that can be handled by this endpoint. */
  lazy val methods: List[RequestMethod] = RequestMethod.all.filter(routes.isDefinedAt)

  /** The value that will be the path of the Http request. */
  def endpoint: RequestEndpoint.Endpoint

  /** Summary of this endpoint, used during Swagger (Open API) document generation. */
  def summary: Option[String] = None

  /** Description of this endpoint, used during Swagger (Open API) document generation. */
  def description: Option[String] = None

  /** Corresponding logic for each method of this endpoint. */
  def routes: Routes[F, Param]

  /** Combine endpoints and logic to generate HttpRoutes. */
  final def toHttpRoutes(): HttpRoutes[F] =
    ServerInterpreter[F]().bindFromRequest[Param](routes, endpoint)
}
