/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import lepus.router.RouterConstructor
import lepus.router.http.{ RequestMethod, RequestEndpoint }

/** Model for generating Swagger documentation for a single endpoint path
  *
  * @param summary
  *   Summary of this endpoint
  * @param description
  *   Description of this endpoint
  * @param tags
  *   Value to classify endpoints
  * @param deprecated
  *   indicate whether this endpoint is deprecated or not
  * @param parameters
  *   List of parameters that can be handled by this endpoint
  * @param responses
  *   List of response values per endpoint status
  */
final case class Path(
  summary:     Option[String]  = None,
  description: Option[String]  = None,
  tags:        Set[String]     = Set.empty,
  deprecated:  Option[Boolean] = None,
  parameters:  List[Parameter] = List.empty,
  responses:   ListMap[String, Response]
)

object Path {

  def fromEndpoint[F[_]](method: RequestMethod, router: RouterConstructor[F]): Path = {
    val endpoints: Vector[RequestEndpoint.Endpoint] = router.endpoint.asVector()
    val parameters: List[Parameter] = endpoints.flatMap {
      case e: RequestEndpoint.Path with RequestEndpoint.Param  => Some(Parameter.fromRequestEndpoint(e))
      case e: RequestEndpoint.Query with RequestEndpoint.Param => Some(Parameter.fromRequestEndpoint(e))
      case _                                                   => None
    }.toList

    val responses = router.responses
      .lift(method)
      .map(resList => resList.map(res => res.status.code.toString -> Response.build(res)))
      .getOrElse(List("default" -> Response.empty))

    Path(
      summary     = router.summary,
      description = router.description,
      tags        = router.tags.map(_.name),
      deprecated  = router.deprecated,
      parameters  = parameters,
      responses   = responses.to(ListMap)
    )
  }
}
