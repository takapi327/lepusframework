/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import lepus.router.model.Schema
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
  content:     ListMap[String, Response.Content],
  description: String
)

object Response {

  val empty = Response(ListMap.empty, ListMap.empty, "Response is not specified")

  val schemaToOpenApiSchema = new SchemaToOpenApiSchema()

  def build(res: RouterResponse[_]): Response = {
    val headers = res.headers
      .map(header => header.name -> Header(schemaToOpenApiSchema(header.schema), header.description))
      .to(ListMap)
    Response(
      headers     = headers,
      content     = ListMap("application/json" -> Content.build(res.schema)),
      description = res.description
    )
  }

  case class Header(
    schema:      Either[String, OpenApiSchema],
    description: String
  )

  /** @param schema
   *   The schema defining the content of the request, response, or parameter.
   * @param examples
   *   Example of the media type. The example object SHOULD be in the correct format as specified by the media type. The
   *   example field is mutually exclusive of the examples field. Furthermore, if referencing a schema which contains an
   *   example, the example value SHALL override the example provided by the schema.
   */
  final case class Content(
    schema:   Option[Either[String, OpenApiSchema]] = None,
    examples: ListMap[String, String]               = ListMap.empty
  )

  object Content {

    def build(schema: Schema[_]): Content =
      Content(
        schema   = Some(schemaToOpenApiSchema(schema)),
        examples = ListMap.empty
      )
  }
}
