/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import scala.collection.immutable.ListMap

import lepus.router.model.{ Schema, SchemaType }

import lepus.swagger.model.{ OpenApiSchema, Reference }
import OpenApiSchema.{ SchemaType => OpenApiSchemaType, SchemaFormat => OpenApiSchemaFormat }

/** Class for converting Schema of Router to Schema for OpenAPI.
  *
  * @param schemaToReference
  *   Class for converting to Reference model
  */
class SchemaToOpenApiSchema(schemaToReference: SchemaToReference):
  def apply[T](
    schema:      Schema[T],
    isOptional:  Boolean = false,
    forceCreate: Boolean = true
  ): Either[Reference, OpenApiSchema] =
    val result =
      if forceCreate then discriminateBySchemaType(schema.schemaType)
      else
        schema.name match
          case Some(name) =>
            schemaToReference.map(name) match
              case Some(value) => Left(value)
              case None        => discriminateBySchemaType(schema.schemaType)

          case None => discriminateBySchemaType(schema.schemaType)

    if isOptional then result.map(_.copy(nullable = Some(isOptional)))
    else result

  private def discriminateBySchemaType(schemaType: SchemaType[_]): Either[Reference, OpenApiSchema] =
    schemaType match
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
        Right(OpenApiSchema(OpenApiSchemaType.String).copy(format = Some(OpenApiSchemaFormat.Binary)))
      case SchemaType.SDate() =>
        Right(OpenApiSchema(OpenApiSchemaType.String).copy(format = Some(OpenApiSchemaFormat.Date)))
      case SchemaType.SDateTime() =>
        Right(OpenApiSchema(OpenApiSchemaType.String).copy(format = Some(OpenApiSchemaFormat.DateTime)))
      case SchemaType.Trait(schemas) => Right(OpenApiSchema(schemas.map(apply(_))))

  private def extractProperties(
    fields: List[SchemaType.Entity.Field]
  ): ListMap[String, Either[Reference, OpenApiSchema]] =
    fields
      .map(field => {
        field.schema match
          case Schema(_, Some(name), _, _) =>
            field.name.encodedName -> Left(schemaToReference.map(name).getOrElse(Reference(name.fullName)))
          case schema => field.name.encodedName -> apply(schema)
      })
      .toListMap
