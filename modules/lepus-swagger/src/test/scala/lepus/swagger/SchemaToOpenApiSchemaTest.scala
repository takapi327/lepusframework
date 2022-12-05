/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import java.util.Date
import java.time.LocalDateTime

import org.specs2.mutable.Specification

import lepus.core.generic.Schema
import lepus.swagger.model.OpenApiSchema.{ SchemaFormat, SchemaType }

object SchemaToOpenApiSchemaTest extends Specification:

  val schemaToTuple         = SchemaToTuple()
  val userSchemaTuples      = schemaToTuple(summon[Schema[User]])
  val schemaToReference     = SchemaToReference(Some(userSchemaTuples.toListMap))
  val schemaToOpenApiSchema = SchemaToOpenApiSchema(schemaToReference)

  "Schema can be converted to OpenAPISchema." should {
    schemaToOpenApiSchema(summon[Schema[User]]).isRight
  }

  "If the model has a Schema Name, it will be Left." should {
    schemaToOpenApiSchema(summon[Schema[User]], false, false).isLeft
  }

  "If the model does not have a Schema Name, it will be Right." should {
    schemaToOpenApiSchema(summon[Schema[String]], false, false).isRight
  }

  "For Option types, the value of nullable is true." should {
    schemaToOpenApiSchema(summon[Schema[Option[String]]]) match
      case Right(s) => s.nullable.getOrElse(false)
      case Left(_)  => false
  }

  "For types other than Option, the value of nullable is None." should {
    schemaToOpenApiSchema(summon[Schema[String]]) match
      case Right(s) => s.nullable.isEmpty
      case Left(_)  => false
  }

  "The Type of String will be string." should {
    schemaToOpenApiSchema(summon[Schema[String]]) match
      case Right(s) => s.`type`.contains(SchemaType.String)
      case Left(_)  => false
  }

  "The Type of Int will be integer." should {
    schemaToOpenApiSchema(summon[Schema[Int]]) match
      case Right(s) => s.`type`.contains(SchemaType.Integer)
      case Left(_)  => false
  }

  "The Type of Double will be number." should {
    schemaToOpenApiSchema(summon[Schema[Double]]) match
      case Right(s) => s.`type`.contains(SchemaType.Number)
      case Left(_)  => false
  }

  "The Type of Boolean will be boolean." should {
    schemaToOpenApiSchema(summon[Schema[Boolean]]) match
      case Right(s) => s.`type`.contains(SchemaType.Boolean)
      case Left(_)  => false
  }

  "The Type of Case Class will be object." should {
    schemaToOpenApiSchema(summon[Schema[User]]) match
      case Right(s) => s.`type`.contains(SchemaType.Object)
      case Left(_)  => false
  }

  "The Type of Array will be array." should {
    schemaToOpenApiSchema(summon[Schema[Array[String]]]) match
      case Right(s) => s.`type`.contains(SchemaType.Array)
      case Left(_)  => false
  }

  "Type of Array[Byte] type becomes string and format becomes binary." should {
    schemaToOpenApiSchema(summon[Schema[Array[Byte]]]) match
      case Right(s) => s.`type`.contains(SchemaType.String) && s.format.contains(SchemaFormat.Binary)
      case Left(_)  => false
  }

  "Type of Date type becomes string and format becomes date." should {
    schemaToOpenApiSchema(summon[Schema[Date]]) match
      case Right(s) => s.`type`.contains(SchemaType.String) && s.format.contains(SchemaFormat.Date)
      case Left(_)  => false
  }

  "Type of LocalDateTime type becomes string but format becomes not date-time." should {
    schemaToOpenApiSchema(summon[Schema[LocalDateTime]]) match
      case Right(s) => s.`type`.contains(SchemaType.String) && !s.format.contains(SchemaFormat.DateTime)
      case Left(_)  => false
  }
