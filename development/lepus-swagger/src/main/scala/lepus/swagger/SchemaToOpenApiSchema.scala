/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import lepus.router.model.{ Schema, SchemaType }

import lepus.swagger.model.OpenApiSchema
import OpenApiSchema.{ SchemaType => OpenApiSchemaType, SchemaFormat => OpenApiSchemaFormat }

class SchemaToOpenApiSchema {
  def apply[T](schema: Schema[T], isOptional: Boolean = false): Either[String, OpenApiSchema] = {
    val result = schema.schemaType match {
      case SchemaType.SInteger() => Right(OpenApiSchema(OpenApiSchemaType.Integer))
      case SchemaType.SNumber()  => Right(OpenApiSchema(OpenApiSchemaType.Number))
      case SchemaType.SBoolean() => Right(OpenApiSchema(OpenApiSchemaType.Boolean))
      case SchemaType.SString()  => Right(OpenApiSchema(OpenApiSchemaType.String))
      case e @ SchemaType.Entity(fields) =>
        Right(
          OpenApiSchema(OpenApiSchemaType.Object).copy(
            required   = e.required.map(_.encodedName),
            properties = extractProperties(fields)
          )
        )
      case SchemaType.SArray(_)       => Right(OpenApiSchema(OpenApiSchemaType.Array))
      case SchemaType.SOption(schema) => apply(schema, true)
      case SchemaType.SBinary() =>
        Right(OpenApiSchema(OpenApiSchemaType.String).copy(format = OpenApiSchemaFormat.Binary))
      case SchemaType.SDate() => Right(OpenApiSchema(OpenApiSchemaType.String).copy(format = OpenApiSchemaFormat.Date))
      case SchemaType.SDateTime() =>
        Right(OpenApiSchema(OpenApiSchemaType.String).copy(format = OpenApiSchemaFormat.DateTime))
      case SchemaType.Trait(schemas) => Right(OpenApiSchema(schemas.map(apply(_))))
    }
    result.map(_.copy(nullable = Some(isOptional)))
  }

  private def extractProperties[T](fields: List[SchemaType.Entity.Field[T]]) =
    fields
      .map(field => {
        field.schema match {
          case Schema(_, Some(name), _, _) => field.name.encodedName -> Left("")
          case schema                      => field.name.encodedName -> apply(schema)
        }
      })
      .toListMap
}
