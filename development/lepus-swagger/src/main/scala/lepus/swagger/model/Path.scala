/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.swagger.model

import io.circe._
import io.circe.generic.semiauto._

import lepus.router.http.RequestEndpoint
import lepus.router.RouterConstructor

/**
 * Model for generating Swagger documentation for a single endpoint path
 *
 * @param summary     Summary of this endpoint
 * @param description Description of this endpoint
 * @param tags        Value to classify endpoints
 * @param deprecated  indicate whether this endpoint is deprecated or not
 * @param parameters  List of parameters that can be handled by this endpoint
 * @param responses   List of response values per endpoint status
 */
final case class Path(
  summary:     Option[String]        = None,
  description: Option[String]        = None,
  tags:        Set[String]           = Set.empty,
  deprecated:  Option[Boolean]       = None,
  parameters:  List[Parameter]       = List.empty,
  //requestBody: Option[RequestBody]  = None,
  responses:   Map[String, Response] = Map.empty,
  //security:    Option[Security]        = None
)

object Path {
  implicit lazy val encoder: Encoder[Path] = deriveEncoder

  def fromEndpoint[F[_]](router: RouterConstructor[F]): Path = {
    val endpoints: Vector[RequestEndpoint.Endpoint] = router.endpoint.asVector()
    val parameters: List[Parameter] = endpoints.flatMap {
      case e: RequestEndpoint.Path  with RequestEndpoint.Param => Some(Parameter.fromRequestEndpoint(e))
      case e: RequestEndpoint.Query with RequestEndpoint.Param => Some(Parameter.fromRequestEndpoint(e))
      case _                                                   => None
    }.toList

    Path(
      summary     = router.summary,
      description = router.description,
      tags        = router.tags.map(_.name),
      deprecated  = router.deprecated,
      parameters  = parameters
    )
  }
}

final case class Response()
object Response {
  implicit lazy val encoder: Encoder[Response] = deriveEncoder
}
