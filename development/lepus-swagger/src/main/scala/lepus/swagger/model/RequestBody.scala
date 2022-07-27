/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import lepus.router.http.Request

import lepus.swagger.SchemaToOpenApiSchema

case class RequestBody(
  description: String,
  required:    Boolean,
  content:     ListMap[String, Content]
)

object RequestBody:

  val empty = RequestBody("RequestBody is not specified", false, ListMap.empty)

  def build[T](body: Request.Body[T], schemaToOpenApiSchema: SchemaToOpenApiSchema): RequestBody =
    RequestBody(
      description = body.description,
      required    = true,
      content     = ListMap("application/json" -> Content.build(body.schema, schemaToOpenApiSchema))
    )
