/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import OpenApiSchema._
case class OpenApiSchema(
  `type`:     Option[SchemaType] = None,
  required:   List[String] = List.empty,
  properties: ListMap[String, Either[String, OpenApiSchema]] = ListMap.empty,
  format: Option[String] = None,
  nullable: Option[Boolean] = None,
  oneOf: List[Either[String, OpenApiSchema]] = List.empty,
)

object OpenApiSchema {

  def apply(`type`: SchemaType): OpenApiSchema =
    OpenApiSchema(`type` = Some(`type`))

  def apply(references: List[Either[String, OpenApiSchema]]): OpenApiSchema =
    OpenApiSchema(oneOf = references)

  sealed abstract class SchemaType(val value: String)
  object SchemaType {
    case object Boolean extends SchemaType("boolean")
    case object Object extends SchemaType("object")
    case object Array extends SchemaType("array")
    case object Number extends SchemaType("number")
    case object String extends SchemaType("string")
    case object Integer extends SchemaType("integer")
  }

  object SchemaFormat {
    val Int32: Option[String] = Some("int32")
    val Int64: Option[String] = Some("int64")
    val Float: Option[String] = Some("float")
    val Double: Option[String] = Some("double")
    val Byte: Option[String] = Some("byte")
    val Binary: Option[String] = Some("binary")
    val Date: Option[String] = Some("date")
    val DateTime: Option[String] = Some("date-time")
    val Password: Option[String] = Some("password")
  }
}
