/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import lepus.router.http.Request
import lepus.core.generic.Schema

import lepus.swagger.SchemaToOpenApiSchema

case class RequestBody[T](
  description:      String,
  required:         Boolean
)(using val schema: Schema[T])

object RequestBody:

  def apply[T: Schema](
    description: String,
    required:    Boolean = true
  ): RequestBody[T] = RequestBody(description, required)

  private[lepus] case class UI(
    description: String,
    required:    Boolean,
    content:     ListMap[String, Content]
  )

  private[lepus] object UI:
    val empty = UI("RequestBody is not specified", false, ListMap.empty)

    def apply[T](
      body:   RequestBody[T],
      schema: SchemaToOpenApiSchema
    ): UI = UI(
      description = body.description,
      required    = body.required,
      content     = ListMap("application/json" -> Content.build(body.schema, schema))
    )
