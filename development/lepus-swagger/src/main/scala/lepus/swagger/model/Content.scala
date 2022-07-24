/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import lepus.router.model.Schema

import lepus.swagger.SchemaToOpenApiSchema

/** @param schema
  *   The schema defining the content of the request, response, or parameter.
  * @param examples
  *   Example of the media type. The example object SHOULD be in the correct format as specified by the media type. The
  *   example field is mutually exclusive of the examples field. Furthermore, if referencing a schema which contains an
  *   example, the example value SHALL override the example provided by the schema.
  */
final case class Content(
  schema:   Option[Either[Reference, OpenApiSchema]] = None,
  examples: ListMap[String, String]                  = ListMap.empty
)

object Content:

  def build(schema: Schema[_], schemaToOpenApiSchema: SchemaToOpenApiSchema): Content =
    Content(
      schema   = Some(schemaToOpenApiSchema(schema, false, false)),
      examples = ListMap.empty
    )
