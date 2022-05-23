/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.model

import java.time._
import java.util.{ Date, UUID }
import java.math.{ BigDecimal => JBigDecimal, BigInteger => JBigInteger }

import scala.concurrent.duration

import SchemaType._
final case class Schema[T](
  schemaType: SchemaType[T],
  name:       Option[Schema.Name] = None,
  format:     Option[String]      = None,
  isOptional: Boolean             = false
) {

  def thisType: String = s"schema is $schemaType"

  def format(f: String): Schema[T] = copy(format = Some(f))

  /** Returns an optional version of this schema, with `isOptional` set to true. */
  def asOption: Schema[Option[T]] =
    Schema(
      schemaType = SOption(this),
      isOptional = true,
      format     = format
    )

  /** Returns an array version of this schema, with the schema type wrapped in [[SchemaType.SArray]]. Sets `isOptional`
    * to true as the collection might be empty.
    */
  def asArray: Schema[Array[T]] =
    Schema(
      schemaType = SArray(this),
      isOptional = true
    )

  /** Returns a collection version of this schema, with the schema type wrapped in [[SchemaType.SArray]]. Sets
    * `isOptional` to true as the collection might be empty.
    */
  def asIterable[C[X] <: Iterable[X]]: Schema[C[T]] =
    Schema(
      schemaType = SArray(this),
      isOptional = true
    )
}

object Schema {

  case class Name(fullName: String, typeParameters: List[String]) {
    val shortName: String = fullName.split('.').lastOption.getOrElse(fullName)
  }

  implicit val schemaString:         Schema[String]            = Schema(SString())
  implicit val schemaByte:           Schema[Byte]              = Schema(SInteger())
  implicit val schemaShort:          Schema[Short]             = Schema(SInteger())
  implicit val schemaInt:            Schema[Int]               = Schema(SInteger[Int]()).format("int32")
  implicit val SchemaLong:           Schema[Long]              = Schema(SInteger[Long]()).format("int64")
  implicit val schemaFloat:          Schema[Float]             = Schema(SNumber[Float]()).format("float")
  implicit val schemaDouble:         Schema[Double]            = Schema(SNumber[Double]()).format("double")
  implicit val schemaBoolean:        Schema[Boolean]           = Schema(SBoolean())
  implicit val schemaByteArray:      Schema[Array[Byte]]       = Schema(SBinary())
  implicit val schemaInstant:        Schema[Instant]           = Schema(SBinary())
  implicit val schemaZonedDateTime:  Schema[ZonedDateTime]     = Schema(SDateTime())
  implicit val schemaOffsetDateTime: Schema[OffsetDateTime]    = Schema(SDateTime())
  implicit val schemaDate:           Schema[Date]              = Schema(SDateTime())
  implicit val SchemaLocalDateTime:  Schema[LocalDateTime]     = Schema(SString())
  implicit val schemaLocalDate:      Schema[LocalDate]         = Schema(SDate())
  implicit val schemaZoneOffset:     Schema[ZoneOffset]        = Schema(SString())
  implicit val schemaJavaDuration:   Schema[Duration]          = Schema(SString())
  implicit val SchemaLocalTime:      Schema[LocalTime]         = Schema(SString())
  implicit val schemaOffsetTime:     Schema[OffsetTime]        = Schema(SString())
  implicit val schemaScalaDuration:  Schema[duration.Duration] = Schema(SString())
  implicit val schemaUUID:           Schema[UUID]              = Schema(SString[UUID]()).format("uuid")
  implicit val schemaBigDecimal:     Schema[BigDecimal]        = Schema(SNumber())
  implicit val schemaJBigDecimal:    Schema[JBigDecimal]       = Schema(SNumber())
  implicit val schemaBigInt:         Schema[BigInt]            = Schema(SInteger())
  implicit val schemaJBigInteger:    Schema[JBigInteger]       = Schema(SInteger())

  implicit def schemaOption[T: Schema]:                        Schema[Option[T]] = implicitly[Schema[T]].asOption
  implicit def schemaArray[T: Schema]:                         Schema[Array[T]]  = implicitly[Schema[T]].asArray
  implicit def schemaIterable[T: Schema, C[X] <: Iterable[X]]: Schema[C[T]]      = implicitly[Schema[T]].asIterable[C]
}
