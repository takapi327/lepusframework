/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import lepus.router.http.{ Response => RouterResponse }

import lepus.swagger.SchemaToOpenApiSchema

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
  headers:     ListMap[String, Response.Header],
  content:     ListMap[String, Content],
  description: String
)

object Response:

  val empty = Response(ListMap.empty, ListMap.empty, "Response is not specified")

  def build(
    res:                   RouterResponse[_],
    schemaToOpenApiSchema: SchemaToOpenApiSchema
  ): Response =
    val headers = res.headers
      .map(header => header.name -> Header(schemaToOpenApiSchema(header.schema), header.description))
      .to(ListMap)
    Response(
      headers     = headers,
      content     = ListMap("application/json" -> Content.build(res.schema, schemaToOpenApiSchema)),
      description = res.description
    )

  case class Header(
    schema:      Either[Reference, OpenApiSchema],
    description: String
  )
