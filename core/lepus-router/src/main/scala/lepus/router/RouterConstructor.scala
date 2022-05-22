/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import cats.effect.{ Async, Sync }

import org.http4s.{ HttpRoutes => Http4sRoutes }

import lepus.router.http.{ RequestEndpoint, RequestMethod, Response, ResponseStatus, Header }
import lepus.router.model.{ Tag, ServerResponse }

/** A model that contains one routing information.
  *
  * For example:
  * {{{
  *   // http:localhost:5555/hello/world/lepus
  *   object HelloRoute extends RouterConstructor[IO] {
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
  * @tparam F
  *   the effect type.
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

  /** Alias of ResponseStatus. */
  protected final val responseStatus = ResponseStatus

  /** Alias of ResponseHeader. */
  protected final val responseHeader = Header.ResponseHeader

  /** Alias of ServerResponse. */
  protected final val serverResponse = ServerResponse

  /** List of methods that can be handled by this endpoint. */
  lazy val methods: List[RequestMethod] = RequestMethod.all.filter(routes.isDefinedAt)

  /** The value that will be the path of the Http request. */
  def endpoint: RequestEndpoint.Endpoint

  /** Summary of this endpoint, used during Swagger (Open API) document generation. */
  def summary: Option[String] = None

  /** Description of this endpoint, used during Swagger (Open API) document generation. */
  def description: Option[String] = None

  /** Tag of this endpoint, used during Swagger (Open API) document generation. */
  def tags: Set[Tag] = Set.empty[Tag]

  /** A flag used during Swagger (Open API) document generation to indicate whether this endpoint is deprecated or not.
    */
  def deprecated: Option[Boolean] = None

  /** An array of responses returned by each method. */
  def responses: HttpResponse[List[Response[_]]] = PartialFunction.empty

  /** Corresponding logic for each method of this endpoint. */
  def routes: HttpRoutes[F, Param]

  /** Combine endpoints and logic to generate HttpRoutes. */
  final def toHttpRoutes: Http4sRoutes[F] =
    ServerInterpreter[F]().bindFromRequest[Param](routes, endpoint)
}
