/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import org.http4s.Method

import lepus.router.*
import lepus.router.internal.*
import lepus.router.http.Endpoint

import lepus.swagger.{ OpenApiConstructor, SchemaToOpenApiSchema }

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
  summary:     Option[String]                      = None,
  description: Option[String]                      = None,
  tags:        Set[String]                         = Set.empty,
  deprecated:  Option[Boolean]                     = None,
  parameters:  List[Parameter]                     = List.empty,
  requestBody: Option[RequestBody.UI]              = None,
  responses:   ListMap[String, OpenApiResponse.UI] = ListMap.empty
)

private[lepus] object Path:

  def fromEndpoint[F[_]](
    method:   Method,
    endpoint: Endpoint[?],
    router:   OpenApiConstructor[F, ?],
    schema:   SchemaToOpenApiSchema
  ): Path =
    val endpoints: Vector[Endpoint[?]] = endpoint.asVector()
    val parameters: List[Parameter] = endpoints.flatMap {
      case e: (Endpoint.Path[?] & Endpoint.Param[?]) =>
        Some(Parameter.fromEndpoint(e, schema).asInstanceOf[Parameter])
      case e: (Endpoint.Query[?] & Endpoint.Param[?]) =>
        Some(Parameter.fromEndpoint(e, schema).asInstanceOf[Parameter])
      case _ => None
    }.toList

    val requestBody = router.bodies
      .lift(method)
      .map(req => RequestBody.UI(req, schema))

    val responses = router.responses
      .lift(method)
      .filter(_.nonEmpty)
      .map(resList => resList.map(res => res.status.code.toString -> res.toUI(schema)))
      .getOrElse(List("default" -> OpenApiResponse.UI.empty))

    Path(
      summary     = router.summary,
      description = router.description,
      tags        = router.tags.map(_.name),
      deprecated  = router.deprecated,
      parameters  = parameters,
      requestBody = requestBody,
      responses   = responses.to(ListMap)
    )
