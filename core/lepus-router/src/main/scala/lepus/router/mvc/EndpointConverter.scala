/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.mvc

import java.time._
import java.util.UUID

import scala.annotation._
import scala.util._

import lepus.router.model._

import scala.reflect.ClassTag

@implicitNotFound("Could not find an implicit EndpointConverter[${S}, ${T}]")
trait EndpointConverter[S, T] {

  def stringTo(from: S): T

  def decode(str: S): DecodeResult[T] = {
    Try { stringTo(str) } match {
      case Success(s) => DecodeResult.Success(s)
      case Failure(e) => DecodeResult.InvalidValue(str.toString, Some(e))
    }
  }

  def schema: Schema
}

object EndpointConverter {

  /** Variable for converting path and query parameter values to any type. */
  implicit val string: EndpointConverter[String, String] = new EndpointConverter[String, String] {
    override def stringTo(str: String): String = str
    override def schema:                Schema = Schema.string
  }

  implicit val byte:    EndpointConverter[String, Byte]    = convertT[String, Byte](_.toByte)(Schema.byteString)
  implicit val short:   EndpointConverter[String, Short]   = convertT[String, Short](_.toShort)(Schema.int32Integer)
  implicit val int:     EndpointConverter[String, Int]     = convertT[String, Int](_.toInt)(Schema.int32Integer)
  implicit val long:    EndpointConverter[String, Long]    = convertT[String, Long](_.toLong)(Schema.int64Integer)
  implicit val float:   EndpointConverter[String, Float]   = convertT[String, Float](_.toFloat)(Schema.floatNumber)
  implicit val double:  EndpointConverter[String, Double]  = convertT[String, Double](_.toDouble)(Schema.doubleNumber)
  implicit val boolean: EndpointConverter[String, Boolean] = convertT[String, Boolean](_.toBoolean)(Schema.boolean)
  implicit val bigDecimal: EndpointConverter[String, BigDecimal] = convertT[String, BigDecimal](BigDecimal(_))
  implicit val localTime:  EndpointConverter[String, LocalTime]  = convertT[String, LocalTime](LocalTime.parse(_))
  implicit val localDate: EndpointConverter[String, LocalDate] =
    convertT[String, LocalDate](LocalDate.parse(_))(Schema.dateString)
  implicit val localDateTime: EndpointConverter[String, LocalDateTime] =
    convertT[String, LocalDateTime](LocalDateTime.parse(_))
  implicit val offsetTime: EndpointConverter[String, OffsetTime] = convertT[String, OffsetTime](OffsetTime.parse(_))
  implicit val offsetDateTime: EndpointConverter[String, OffsetDateTime] =
    convertT[String, OffsetDateTime](OffsetDateTime.parse(_))(Schema.dateTimeString)
  implicit val zonedDateTime: EndpointConverter[String, ZonedDateTime] =
    convertT[String, ZonedDateTime](ZonedDateTime.parse(_))(Schema.dateTimeString)
  implicit val instant: EndpointConverter[String, Instant] =
    convertT[String, Instant](Instant.parse(_))(Schema.dateTimeString)
  implicit val uuid: EndpointConverter[String, UUID] = convertT[String, UUID](UUID.fromString)(Schema.uuidString)

  /** Variables for converting path and query parameter values to arrays of any type. */
  implicit def array[T](implicit
    converter: EndpointConverter[String, T],
    classTag:  ClassTag[T]
  ): EndpointConverter[String, Array[T]] =
    convertT(_.split(",").map(converter.stringTo))
  implicit def list[T](implicit
    converter: EndpointConverter[String, T],
    classTag:  ClassTag[T]
  ): EndpointConverter[String, List[T]] =
    convertT(array[T].stringTo(_).toList)
  implicit def seq[T](implicit
    converter: EndpointConverter[String, T],
    classTag:  ClassTag[T]
  ): EndpointConverter[String, Seq[T]] =
    convertT(array[T].stringTo(_).toSeq)
  implicit def set[T](implicit
    converter: EndpointConverter[String, T],
    classTag:  ClassTag[T]
  ): EndpointConverter[String, Set[T]] =
    convertT(array[T].stringTo(_).toSet)

  /** Converted from String to any type and finally to DecodeResult
    *
    * @param stringT
    *   Convert from String to type T
    * @tparam T
    *   Type of String to be converted
    * @return
    *   DecodeResult Success storing T type
    */
  def stringTo[T](stringT: String => T): String => DecodeResult[T] =
    stringT.andThen(DecodeResult.Success(_))

  /** Generate an EndpointConverter that performs the conversion of type S to type T.
    *
    * @param st
    *   Process to convert type S to type T
    * @param s
    *   Character code form of the value converted from S to T. Mainly used when generating Swagger (Open API)
    *   documents.
    * @tparam S
    *   Http request path and query parameter values. This is mainly a String.
    * @tparam T
    *   Type to convert Http request path and query parameter values.
    * @return
    *   EndpointConverter to perform the conversion process from type S to type T
    */
  def convertT[S, T](st: S => T)(implicit s: Schema = Schema.string): EndpointConverter[S, T] =
    new EndpointConverter[S, T] {
      override def stringTo(str: S): T      = st(str)
      override def schema:           Schema = s
    }
}
