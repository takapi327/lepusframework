/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import OpenApiSchema._
case class OpenApiSchema(
  `type`:     Option[SchemaType]                                = None,
  required:   List[String]                                      = List.empty,
  properties: ListMap[String, Either[Reference, OpenApiSchema]] = ListMap.empty,
  format:     Option[String]                                    = None,
  nullable:   Option[Boolean]                                   = None,
  oneOf:      List[Either[Reference, OpenApiSchema]]            = List.empty
)

object OpenApiSchema {

  def apply(`type`: SchemaType): OpenApiSchema =
    OpenApiSchema(`type` = Some(`type`))

  def apply(references: List[Either[Reference, OpenApiSchema]]): OpenApiSchema =
    OpenApiSchema(oneOf = references)

  sealed abstract class SchemaType(val value: String)
  object SchemaType {
    case object Boolean extends SchemaType("boolean")
    case object Object  extends SchemaType("object")
    case object Array   extends SchemaType("array")
    case object Number  extends SchemaType("number")
    case object String  extends SchemaType("string")
    case object Integer extends SchemaType("integer")
  }

  object SchemaFormat {
    val Int32:    String = "int32"
    val Int64:    String = "int64"
    val Float:    String = "float"
    val Double:   String = "double"
    val Byte:     String = "byte"
    val Binary:   String = "binary"
    val Date:     String = "date"
    val DateTime: String = "date-time"
    val Password: String = "password"
  }
}
