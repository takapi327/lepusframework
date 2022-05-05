/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import io.circe._
import io.circe.generic.semiauto._

import lepus.router.RouterConstructor
import lepus.router.http.RequestEndpoint

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
  // requestBody: Option[RequestBody]  = None,
  responses: Map[String, Response] = Map.empty
  // security:    Option[Security]        = None
)

object Path {
  implicit lazy val encoder: Encoder[Path] = deriveEncoder

  def fromEndpoint[F[_]](router: RouterConstructor[F]): Path = {
    val endpoints: Vector[RequestEndpoint.Endpoint] = router.endpoint.asVector()
    val parameters: List[Parameter] = endpoints.flatMap {
      case e: RequestEndpoint.Path with RequestEndpoint.Param  => Some(Parameter.fromRequestEndpoint(e))
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

/** @param schema
  *   The schema defining the content of the request, response, or parameter.
  * @param examples
  *   Example of the media type. The example object SHOULD be in the correct format as specified by the media type. The
  *   example field is mutually exclusive of the examples field. Furthermore, if referencing a schema which contains an
  *   example, the example value SHALL override the example provided by the schema.
  */
final case class Content(
  schema:   Map[String, String],
  examples: Map[String, String]
)

object Content {
  implicit lazy val encoder: Encoder[Content] = deriveEncoder
}

/** @param headers
  *   Maps a header name to its definition. RFC7230 states header names are case insensitive. If a response header is
  *   defined with the name "Content-Type", it SHALL be ignored.
  * @param content
  *   A map containing descriptions of potential response payloads. The key is a media type or media type range and the
  *   value describes it. For responses that match multiple keys, only the most specific key is applicable. e.g.
  *   text/plain overrides text
  * @param description
  *   REQUIRED. A short description of the response. CommonMark syntax MAY be used for rich text representation.
  */
final case class Response(
  headers:     Map[String, String],
  content:     Map[String, Content],
  description: String
)

object Response {
  implicit lazy val encoder: Encoder[Response] = deriveEncoder
}
