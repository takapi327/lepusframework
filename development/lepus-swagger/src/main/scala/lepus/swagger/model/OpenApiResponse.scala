/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import io.circe.Encoder

import org.http4s.Status

import lepus.router.http.{ Header as RouteHeader }
import lepus.router.model.Schema

import lepus.swagger.SchemaToOpenApiSchema

/** API Response Value
  *
  * @param status
  *   Response Status Code
  * @param headers
  *   List of headers given to the response
  * @param description
  *   Response Description
  */
case class OpenApiResponse[T: Encoder: Schema](
  status:      Status,
  headers:     List[RouteHeader.CustomHeader[?]] = List.empty,
  description: String
):
  val schema: Schema[T] = summon[Schema[T]]

  def toUI(schemaToOpenApiSchema: SchemaToOpenApiSchema): OpenApiResponse.UI =
    val headerList = headers
      .map(header =>
        header.name.name -> OpenApiResponse.Header(schemaToOpenApiSchema(header.schema), header.description)
      )
      .to(ListMap)
    OpenApiResponse.UI(
      headers     = headerList,
      content     = ListMap("application/json" -> Content.build(schema, schemaToOpenApiSchema)),
      description = description
    )

object OpenApiResponse:

  /** @param headers
    *   Maps a header name to its definition. RFC7230 states header names are case insensitive. If a response header is
    *   defined with the name "Content-Type", it SHALL be ignored.
    * @param content
    *   A map containing descriptions of potential response payloads. The key is a media type or media type range and
    *   the value describes it. For responses that match multiple keys, only the most specific key is applicable. e.g.
    *   text/plain overrides text
    * @param description
    *   REQUIRED. A short description of the response. CommonMark syntax MAY be used for rich text representation.
    */
  case class UI(
    headers:     ListMap[String, OpenApiResponse.Header],
    content:     ListMap[String, Content],
    description: String
  )

  object UI:
    val empty: UI = UI(ListMap.empty, ListMap.empty, "Response is not specified")

  case class Header(
    schema:      Either[Reference, OpenApiSchema],
    description: String
  )
end OpenApiResponse
