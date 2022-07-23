/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import java.time.*
import java.util.UUID

import scala.annotation.*
import scala.reflect.ClassTag
import scala.util.*

import lepus.router.model.*
import Schema.*

@implicitNotFound("Could not find an implicit EndpointConverter[${S}, ${T}]")
trait EndpointConverter[S, T]:

  def stringTo(from: S): T

  def decode(str: S): DecodeResult[T] =
    Try { stringTo(str) } match
      case Success(s) => DecodeResult.Success(s)
      case Failure(e) => DecodeResult.InvalidValue(str.toString, Some(e))

  def schema: Schema[T]

object EndpointConverter:

  /** Variable for converting path and query parameter values to any type. */
  given EndpointConverter[String, String] with
    override def stringTo(from: String): String = from
    override def schema: Schema[String] = summon[Schema[String]]

  given EndpointConverter[String, Byte]           = convertT[String, Byte](_.toByte)
  given EndpointConverter[String, Short]          = convertT[String, Short](_.toShort)
  given EndpointConverter[String, Int]            = convertT[String, Int](_.toInt)
  given EndpointConverter[String, Long]           = convertT[String, Long](_.toLong)
  given EndpointConverter[String, Float]          = convertT[String, Float](_.toFloat)
  given EndpointConverter[String, Double]         = convertT[String, Double](_.toDouble)
  given EndpointConverter[String, Boolean]        = convertT[String, Boolean](_.toBoolean)
  given EndpointConverter[String, BigDecimal]     = convertT[String, BigDecimal](BigDecimal(_))
  given EndpointConverter[String, LocalTime]      = convertT[String, LocalTime](LocalTime.parse(_))
  given EndpointConverter[String, LocalDate]      = convertT[String, LocalDate](LocalDate.parse(_))
  given EndpointConverter[String, LocalDateTime]  = convertT[String, LocalDateTime](LocalDateTime.parse(_))
  given EndpointConverter[String, OffsetTime]     = convertT[String, OffsetTime](OffsetTime.parse(_))
  given EndpointConverter[String, OffsetDateTime] = convertT[String, OffsetDateTime](OffsetDateTime.parse(_))
  given EndpointConverter[String, ZonedDateTime]  = convertT[String, ZonedDateTime](ZonedDateTime.parse(_))
  given EndpointConverter[String, Instant]        = convertT[String, Instant](Instant.parse(_))
  given EndpointConverter[String, UUID]           = convertT[String, UUID](UUID.fromString)

  /** Variables for converting path and query parameter values to arrays of any type. */
  given [T](using
    classTag: ClassTag[T],
    schema:   Schema[T]
  ): EndpointConverter[String, Array[T]] =
    convertT(_.split(",").map(_.asInstanceOf[T]))
  given [T](using
    classTag: ClassTag[T],
    schema:   Schema[T]
  ): EndpointConverter[String, List[T]] =
    convertT(summon[EndpointConverter[String, Array[T]]].stringTo(_).toList)
  given [T](using
    classTag: ClassTag[T],
    schema:   Schema[T]
  ): EndpointConverter[String, Seq[T]] =
    convertT(summon[EndpointConverter[String, Array[T]]].stringTo(_).toSeq)
  given [T](using
    classTag: ClassTag[T],
    schema:   Schema[T]
  ): EndpointConverter[String, Set[T]] =
    convertT(summon[EndpointConverter[String, Array[T]]].stringTo(_).toSet)

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
  def convertT[S, T](st: S => T)(using s: Schema[T]): EndpointConverter[S, T] =
    new EndpointConverter[S, T] {
      override def stringTo(str: S): T         = st(str)
      override def schema:           Schema[T] = s
    }
