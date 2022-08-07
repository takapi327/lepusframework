/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import OpenApiSchema.*
case class OpenApiSchema(
  `type`:     Option[SchemaType]                                = None,
  required:   List[String]                                      = List.empty,
  properties: ListMap[String, Either[Reference, OpenApiSchema]] = ListMap.empty,
  format:     Option[SchemaFormat]                              = None,
  nullable:   Option[Boolean]                                   = None,
  oneOf:      List[Either[Reference, OpenApiSchema]]            = List.empty
)

private[lepus] object OpenApiSchema:

  def apply(`type`: SchemaType): OpenApiSchema =
    OpenApiSchema(`type` = Some(`type`))

  def apply(references: List[Either[Reference, OpenApiSchema]]): OpenApiSchema =
    OpenApiSchema(oneOf = references)

  enum SchemaType(`type`: String):
    override def toString: String = `type`
    case Boolean extends SchemaType("boolean")
    case Object  extends SchemaType("object")
    case Array   extends SchemaType("array")
    case Number  extends SchemaType("number")
    case String  extends SchemaType("string")
    case Integer extends SchemaType("integer")

  enum SchemaFormat(`type`: String):
    override def toString: String = `type`
    case Int32    extends SchemaFormat("int32")
    case Int64    extends SchemaFormat("int64")
    case Float    extends SchemaFormat("float")
    case Double   extends SchemaFormat("double")
    case Byte     extends SchemaFormat("byte")
    case Binary   extends SchemaFormat("binary")
    case Date     extends SchemaFormat("date")
    case DateTime extends SchemaFormat("date-time")
    case Password extends SchemaFormat("password")
