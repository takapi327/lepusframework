/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router.model

import java.time.*
import java.util.{ Date, UUID }
import java.math.{ BigDecimal => JBigDecimal, BigInteger => JBigInteger }

import scala.concurrent.duration.{ Duration => ScalaDuration }

import SchemaType.*

final case class Schema[T](
  schemaType: SchemaType[T],
  name:       Option[Schema.Name] = None,
  format:     Option[String]      = None,
  isOptional: Boolean             = false
):
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

object Schema:
  case class Name(fullName: String, typeParameters: List[String]) {
    val shortName: String = fullName.split('.').lastOption.getOrElse(fullName)
  }

  given Schema[String]         = Schema(SString())
  given Schema[Byte]           = Schema(SInteger())
  given Schema[Short]          = Schema(SInteger())
  given Schema[Int]            = Schema(SInteger[Int]()).format("int32")
  given Schema[Long]           = Schema(SInteger[Long]()).format("int64")
  given Schema[Float]          = Schema(SNumber[Float]()).format("float")
  given Schema[Double]         = Schema(SNumber[Double]()).format("double")
  given Schema[Boolean]        = Schema(SBoolean())
  given Schema[Array[Byte]]    = Schema(SBinary())
  given Schema[Instant]        = Schema(SBinary())
  given Schema[ZonedDateTime]  = Schema(SDateTime())
  given Schema[OffsetDateTime] = Schema(SDateTime())
  given Schema[Date]           = Schema(SDate())
  given Schema[LocalDateTime]  = Schema(SString())
  given Schema[LocalDate]      = Schema(SDate())
  given Schema[ZoneOffset]     = Schema(SString())
  given Schema[Duration]       = Schema(SString())
  given Schema[LocalTime]      = Schema(SString())
  given Schema[OffsetTime]     = Schema(SString())
  given Schema[ScalaDuration]  = Schema(SString())
  given Schema[UUID]           = Schema(SString[UUID]()).format("uuid")
  given Schema[BigDecimal]     = Schema(SNumber())
  given Schema[JBigDecimal]    = Schema(SNumber())
  given Schema[BigInt]         = Schema(SInteger())
  given Schema[JBigInteger]    = Schema(SInteger())

  given [T: Schema]:                      Schema[Option[T]] = summon[Schema[T]].asOption
  given [T: Schema]:                      Schema[Array[T]]  = summon[Schema[T]].asArray
  given [T: Schema, C[X] <: Iterable[X]]: Schema[C[T]]      = summon[Schema[T]].asIterable[C]
