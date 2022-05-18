/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.model

import java.time._
import java.util.{ Date, UUID }
import java.math.{ BigDecimal => JBigDecimal, BigInteger => JBigInteger }

import io.circe._
import io.circe.generic.semiauto._

final case class Schema(`type`: String, format: Option[String])
object Schema {
  implicit lazy val encoder: Encoder[Schema] = deriveEncoder

  val int32Integer   = Schema(SchemaType.INTEGER, SchemaFormat.INT32)
  val int64Integer   = Schema(SchemaType.INTEGER, SchemaFormat.INT64)
  val floatNumber    = Schema(SchemaType.NUMBER, SchemaFormat.FLOAT)
  val doubleNumber   = Schema(SchemaType.NUMBER, SchemaFormat.DOUBLE)
  val boolean        = Schema(SchemaType.BOOLEAN, None)
  val string         = Schema(SchemaType.STRING, None)
  val byteString     = Schema(SchemaType.STRING, SchemaFormat.BYTE)
  val binaryString   = Schema(SchemaType.STRING, SchemaFormat.BINARY)
  val dateString     = Schema(SchemaType.STRING, SchemaFormat.DATE)
  val dateTimeString = Schema(SchemaType.STRING, SchemaFormat.DATETIME)
  val emailString    = Schema(SchemaType.STRING, SchemaFormat.EMAIL)
  val passwordString = Schema(SchemaType.STRING, SchemaFormat.PASSWORD)
  val uuidString     = Schema(SchemaType.STRING, SchemaFormat.UUID)

  object SchemaType {
    val BOOLEAN = "boolean"
    val OBJECT  = "object"
    val ARRAY   = "array"
    val NUMBER  = "number"
    val STRING  = "string"
    val INTEGER = "integer"
  }

  object SchemaFormat {
    val INT32:    Option[String] = Some("int32")
    val INT64:    Option[String] = Some("int64")
    val FLOAT:    Option[String] = Some("float")
    val DOUBLE:   Option[String] = Some("double")
    val BYTE:     Option[String] = Some("byte")
    val BINARY:   Option[String] = Some("binary")
    val DATE:     Option[String] = Some("date")
    val DATETIME: Option[String] = Some("date-time")
    val EMAIL:    Option[String] = Some("email")
    val PASSWORD: Option[String] = Some("password")
    val UUID:     Option[String] = Some("uuid")
  }
}

import SchemaType._
final case class SchemaL[T](
  schemaType: SchemaType[T],
  name:       Option[SchemaL.Name] = None,
  format:     Option[String]       = None,
  isOptional: Boolean              = false
) {

  def thisType: String = s"schema is $schemaType"

  def format(f: String): SchemaL[T] = copy(format = Some(f))

  /** Returns an optional version of this schema, with `isOptional` set to true. */
  def asOption: SchemaL[Option[T]] =
    SchemaL(
      schemaType = SOption(this),
      isOptional = true,
      format     = format
    )

  /** Returns an array version of this schema, with the schema type wrapped in [[SArray]]. Sets `isOptional` to true as
    * the collection might be empty.
    */
  def asArray: SchemaL[Array[T]] =
    SchemaL(
      schemaType = SArray(this),
      isOptional = true
    )

  /** Returns a collection version of this schema, with the schema type wrapped in [[SArray]]. Sets `isOptional` to true
    * as the collection might be empty.
    */
  def asIterable[C[X] <: Iterable[X]]: SchemaL[C[T]] =
    SchemaL(
      schemaType = SArray(this),
      isOptional = true
    )
}

object SchemaL {

  case class Name(fullName: String, typeParameters: List[String])

  implicit val schemaString:         SchemaL[String]         = SchemaL(SString())
  implicit val schemaByte:           SchemaL[Byte]           = SchemaL(SInteger())
  implicit val schemaShort:          SchemaL[Short]          = SchemaL(SInteger())
  implicit val schemaInt:            SchemaL[Int]            = SchemaL(SInteger[Int]()).format("int32")
  implicit val schemaLong:           SchemaL[Long]           = SchemaL(SInteger[Long]()).format("int64")
  implicit val schemaFloat:          SchemaL[Float]          = SchemaL(SNumber[Float]()).format("float")
  implicit val schemaDouble:         SchemaL[Double]         = SchemaL(SNumber[Double]()).format("double")
  implicit val schemaBoolean:        SchemaL[Boolean]        = SchemaL(SBoolean())
  implicit val schemaUnit:           SchemaL[Unit]           = SchemaL(SUnit())
  implicit val schemaByteArray:      SchemaL[Array[Byte]]    = SchemaL(SBinary())
  implicit val schemaInstant:        SchemaL[Instant]        = SchemaL(SBinary())
  implicit val schemaZonedDateTime:  SchemaL[ZonedDateTime]  = SchemaL(SDateTime())
  implicit val schemaOffsetDateTime: SchemaL[OffsetDateTime] = SchemaL(SDateTime())
  implicit val schemaDate:           SchemaL[Date]           = SchemaL(SDateTime())
  implicit val schemaLocalDateTime:  SchemaL[LocalDateTime]  = SchemaL(SString())
  implicit val schemaLocalDate:      SchemaL[LocalDate]      = SchemaL(SDate())
  implicit val schemaZoneOffset:     SchemaL[ZoneOffset]     = SchemaL(SString())
  implicit val schemaJavaDuration:   SchemaL[Duration]       = SchemaL(SString())
  implicit val schemaLocalTime:      SchemaL[LocalTime]      = SchemaL(SString())
  implicit val schemaOffsetTime:     SchemaL[OffsetTime]     = SchemaL(SString())
  implicit val schemaScalaDuration: SchemaL[scala.concurrent.duration.Duration] = SchemaL(SString())
  implicit val schemaUUID:        SchemaL[UUID]        = SchemaL(SString[UUID]()).format("uuid")
  implicit val schemaBigDecimal:  SchemaL[BigDecimal]  = SchemaL(SNumber())
  implicit val schemaJBigDecimal: SchemaL[JBigDecimal] = SchemaL(SNumber())
  implicit val schemaBigInt:      SchemaL[BigInt]      = SchemaL(SInteger())
  implicit val schemaJBigInteger: SchemaL[JBigInteger] = SchemaL(SInteger())

  implicit def schemaOption[T: SchemaL]: SchemaL[Option[T]] = implicitly[SchemaL[T]].asOption
  implicit def schemaArray[T: SchemaL]:  SchemaL[Array[T]]  = implicitly[SchemaL[T]].asArray
  implicit def schemaIterable[T: SchemaL, C[X] <: Iterable[X]]: SchemaL[C[T]] = implicitly[SchemaL[T]].asIterable[C]
}
