/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.mvc

import java.time._
import java.util._

import scala.annotation._
import scala.util.{ Failure, Success, Try }

import lepus.router.model.{ DecodeResult, Schema }

@implicitNotFound("Could not find an implicit EndpointConverter[${L}, ${H}]")
trait EndpointConverter[L, H] {

  def stringToResult(from: L): DecodeResult[H]

  def decode(str: L): DecodeResult[H] = decode(str, stringToResult)

  def decode(str: L, fromTo: L => DecodeResult[H]): DecodeResult[H] = {
    Try { fromTo(str) } match {
      case Success(s) => s
      case Failure(e) => DecodeResult.InvalidValue(str.toString, e)
    }
  }

  def schema: Schema
}

object EndpointConverter {

  implicit val string: EndpointConverter[String, String] = new EndpointConverter[String, String] {
    override def stringToResult(str: String): DecodeResult[String] = DecodeResult.Success(str)
    override def schema: Schema = Schema.string
  }
  implicit val byte: EndpointConverter[String, Byte] = new EndpointConverter[String, Byte] {
    override def stringToResult(str: String): DecodeResult[Byte] = DecodeResult.Success(str.toByte)
    override def schema: Schema = Schema.byteString
  }
  implicit val short: EndpointConverter[String, Short] = new EndpointConverter[String, Short] {
    override def stringToResult(str: String): DecodeResult[Short] = DecodeResult.Success(str.toShort)
    override def schema: Schema = Schema.int32Integer
  }
  implicit val int: EndpointConverter[String, Int] = new EndpointConverter[String, Int] {
    override def stringToResult(str: String): DecodeResult[Int] = DecodeResult.Success(str.toInt)
    override def schema: Schema = Schema.int32Integer
  }
  implicit val long: EndpointConverter[String, Long] = new EndpointConverter[String, Long] {
    override def stringToResult(str: String): DecodeResult[Long] = DecodeResult.Success(str.toLong)
    override def schema: Schema = Schema.int64Integer
  }
  implicit val float: EndpointConverter[String, Float] = new EndpointConverter[String, Float] {
    override def stringToResult(str: String): DecodeResult[Float] = DecodeResult.Success(str.toFloat)
    override def schema: Schema = Schema.floatNumber
  }
  implicit val double: EndpointConverter[String, Double] = new EndpointConverter[String, Double] {
    override def stringToResult(str: String): DecodeResult[Double] = DecodeResult.Success(str.toDouble)
    override def schema: Schema = Schema.doubleNumber
  }
  implicit val boolean: EndpointConverter[String, Boolean] = new EndpointConverter[String, Boolean] {
    override def stringToResult(str: String): DecodeResult[Boolean] = DecodeResult.Success(str.toBoolean)
    override def schema: Schema = Schema.boolean
  }
  implicit val bigDecimal: EndpointConverter[String, BigDecimal] = new EndpointConverter[String, BigDecimal] {
    override def stringToResult(str: String): DecodeResult[BigDecimal] = DecodeResult.Success(BigDecimal(str))
    override def schema: Schema = Schema.string
  }
  implicit val localTime: EndpointConverter[String, LocalTime] = new EndpointConverter[String, LocalTime] {
    override def stringToResult(str: String): DecodeResult[LocalTime] = DecodeResult.Success(LocalTime.parse(str))
    override def schema: Schema = Schema.string
  }
  implicit val localDate: EndpointConverter[String, LocalDate] = new EndpointConverter[String, LocalDate] {
    override def stringToResult(str: String): DecodeResult[LocalDate] = DecodeResult.Success(LocalDate.parse(str))
    override def schema: Schema = Schema.dateString
  }
  implicit val localDateTime: EndpointConverter[String, LocalDateTime] = new EndpointConverter[String, LocalDateTime] {
    override def stringToResult(str: String): DecodeResult[LocalDateTime] = DecodeResult.Success(LocalDateTime.parse(str))
    override def schema: Schema = Schema.string
  }
  implicit val offsetTime: EndpointConverter[String, OffsetTime] = new EndpointConverter[String, OffsetTime] {
    override def stringToResult(str: String): DecodeResult[OffsetTime] = DecodeResult.Success(OffsetTime.parse(str))
    override def schema: Schema = Schema.string
  }
  implicit val offsetDateTime: EndpointConverter[String, OffsetDateTime] = new EndpointConverter[String, OffsetDateTime] {
    override def stringToResult(str: String): DecodeResult[OffsetDateTime] = DecodeResult.Success(OffsetDateTime.parse(str))
    override def schema: Schema = Schema.dateTimeString
  }
  implicit val zonedDateTime: EndpointConverter[String, ZonedDateTime] = new EndpointConverter[String, ZonedDateTime] {
    override def stringToResult(str: String): DecodeResult[ZonedDateTime] = DecodeResult.Success(ZonedDateTime.parse(str))
    override def schema: Schema = Schema.dateTimeString
  }
  implicit val instant: EndpointConverter[String, Instant] = new EndpointConverter[String, Instant] {
    override def stringToResult(str: String): DecodeResult[Instant] = DecodeResult.Success(Instant.parse(str))
    override def schema: Schema = Schema.dateTimeString
  }
  implicit val uuid: EndpointConverter[String, UUID] = new EndpointConverter[String, UUID] {
    override def stringToResult(str: String): DecodeResult[UUID] = DecodeResult.Success(UUID.fromString(str))
    override def schema: Schema = Schema.uuidString
  }

  def stringTo[T](fromTo: String => T): String => DecodeResult[T] =
    fromTo.andThen(DecodeResult.Success(_))
}
