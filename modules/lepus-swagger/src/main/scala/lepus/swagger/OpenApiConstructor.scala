/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import lepus.router.RouterConstructor
import lepus.router.Http
import lepus.router.http.Request

import lepus.swagger.model.{ Tag, OpenApiResponse, RequestBody }

/** Model for generating OpenApi documentation.
  *
  * For example:
  * {{{
  *   object HelloRoute extends OpenApiConstructor[IO, String]:
  *     override val summary     = Some("Hello world")
  *     override val description = Some("Hello world")
  *     override val tags        = Set(Tag)
  *     override val deprecated  = Some(false)
  *
  *     override def responses = {
  *       case GET => List(
  *         OpenApiResponse[HelloWorld](Status.Ok, List.empty, "Hello world")
  *       )
  *     }
  *
  *     override def routes = {
  *       case GET => IO(Response.NoContent)
  *     }
  * }}}
  *
  * @tparam F
  *   the effect type.
  * @tparam T
  *   Endpoint Type
  */
trait OpenApiConstructor[F[_], T] extends RouterConstructor[F, T]:

  /** Summary of this endpoint, used during Open API document generation. */
  def summary: Option[String] = None

  /** Description of this endpoint, used during Open API document generation. */
  def description: Option[String] = None

  /** Tag of this endpoint, used during Open API document generation. */
  def tags: Set[Tag] = Set.empty[Tag]

  /** The body that each method request receives */
  def bodies: Http[RequestBody[?]] = PartialFunction.empty

  /** An array of responses returned by each method. */
  def responses: Http[List[OpenApiResponse[?]]] = PartialFunction.empty

  /** A flag used during Open API document generation to indicate whether this endpoint is deprecated or not.
    */
  def deprecated: Option[Boolean] = None
