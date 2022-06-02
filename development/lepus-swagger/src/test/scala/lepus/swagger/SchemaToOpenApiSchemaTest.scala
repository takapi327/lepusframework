/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import org.specs2.mutable.Specification

import lepus.router.model.Schema

import lepus.swagger.model.OpenApiSchema.{ SchemaType, SchemaFormat }

object SchemaToOpenApiSchemaTest extends Specification {

  val schemaToTuple         = new SchemaToTuple()
  val userSchemaTuples      = schemaToTuple(User.schema)
  val schemaToReference     = new SchemaToReference(Some(userSchemaTuples.toListMap))
  val schemaToOpenApiSchema = new SchemaToOpenApiSchema(schemaToReference)

  "Schema can be converted to OpenAPISchema." should {
    schemaToOpenApiSchema(User.schema).isRight
  }

  "If the model has a Schema Name, it will be Left." should {
    schemaToOpenApiSchema(User.schema, false, false).isLeft
  }

  "If the model does not have a Schema Name, it will be Right." should {
    schemaToOpenApiSchema(Schema.schemaString, false, false).isRight
  }

  "For Option types, the value of nullable is true." should {
    schemaToOpenApiSchema(Schema.schemaOption[String]) match {
      case Right(s) => s.nullable.getOrElse(false)
      case Left(_)  => false
    }
  }

  "For types other than Option, the value of nullable is None." should {
    schemaToOpenApiSchema(Schema.schemaString) match {
      case Right(s) => s.nullable.isEmpty
      case Left(_)  => false
    }
  }

  "The Type of String will be string." should {
    schemaToOpenApiSchema(Schema.schemaString) match {
      case Right(s) => s.`type`.contains(SchemaType.String)
      case Left(_)  => false
    }
  }

  "The Type of Int will be integer." should {
    schemaToOpenApiSchema(Schema.schemaInt) match {
      case Right(s) => s.`type`.contains(SchemaType.Integer)
      case Left(_)  => false
    }
  }

  "The Type of Double will be number." should {
    schemaToOpenApiSchema(Schema.schemaDouble) match {
      case Right(s) => s.`type`.contains(SchemaType.Number)
      case Left(_)  => false
    }
  }

  "The Type of Boolean will be boolean." should {
    schemaToOpenApiSchema(Schema.schemaBoolean) match {
      case Right(s) => s.`type`.contains(SchemaType.Boolean)
      case Left(_)  => false
    }
  }

  "The Type of Case Class will be object." should {
    schemaToOpenApiSchema(User.schema) match {
      case Right(s) => s.`type`.contains(SchemaType.Object)
      case Left(_)  => false
    }
  }

  "The Type of Array will be array." should {
    schemaToOpenApiSchema(Schema.schemaArray[String]) match {
      case Right(s) => s.`type`.contains(SchemaType.Array)
      case Left(_)  => false
    }
  }

  "Type of Array[Byte] type becomes string and format becomes binary." should {
    schemaToOpenApiSchema(Schema.schemaByteArray) match {
      case Right(s) => s.`type`.contains(SchemaType.String) && s.format.contains(SchemaFormat.Binary)
      case Left(_)  => false
    }
  }

  "Type of Date type becomes string and format becomes date." should {
    schemaToOpenApiSchema(Schema.schemaDate) match {
      case Right(s) => s.`type`.contains(SchemaType.String) && s.format.contains(SchemaFormat.Date)
      case Left(_)  => false
    }
  }

  "Type of LocalDateTime type becomes string but format becomes not date-time." should {
    schemaToOpenApiSchema(Schema.schemaLocalDateTime) match {
      case Right(s) => s.`type`.contains(SchemaType.String) && !s.format.contains(SchemaFormat.DateTime)
      case Left(_)  => false
    }
  }
}
