/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.mvc

import java.time._
import java.util.UUID

import scala.reflect.ClassTag

import org.scalacheck.{ Arbitrary, Gen }
import org.scalacheck.Prop._

import org.scalatest.Assertion
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.Checkers

import lepus.router.model.DecodeResult._

class EndpointConverterTest extends AnyFlatSpec with Matchers with Checkers {

  it should "decode simple types using .toString" in {
    checkDecodeFromString[String]
    checkDecodeFromString[Byte]
    checkDecodeFromString[Short]
    checkDecodeFromString[Int]
    checkDecodeFromString[Long]
    checkDecodeFromString[Float]
    checkDecodeFromString[Double]
    checkDecodeFromString[Boolean]
    checkDecodeFromString[BigDecimal]
    checkDecodeFromString[LocalTime]
    checkDecodeFromString[LocalDate]
    checkDecodeFromString[LocalDateTime]
    checkDecodeFromString[OffsetTime]
    checkDecodeFromString[OffsetDateTime]
    checkDecodeFromString[ZonedDateTime]
    checkDecodeFromString[Instant]
    checkDecodeFromString[UUID]
  }

  /*
  it should "assert array types using .mkString" in {
    checkDecodeFromArray[List, String]()
    checkDecodeFromArray[List, Byte]()
    checkDecodeFromArray[List, Short]()
    checkDecodeFromArray[List, Int]()
    checkDecodeFromArray[List, Long]()
    checkDecodeFromArray[List, Float]()
    checkDecodeFromArray[List, Double]()
    checkDecodeFromArray[List, Boolean]()
    checkDecodeFromArray[List, BigDecimal]()
    checkDecodeFromArray[List, LocalTime]()
    checkDecodeFromArray[List, LocalDate]()
    checkDecodeFromArray[List, LocalDateTime]()
    checkDecodeFromArray[List, OffsetTime]()
    checkDecodeFromArray[List, OffsetDateTime]()
    checkDecodeFromArray[List, ZonedDateTime]()
    checkDecodeFromArray[List, Instant]()
    checkDecodeFromArray[List, UUID]()

    checkDecodeFromArray[Seq, String]()
    checkDecodeFromArray[Seq, Byte]()
    checkDecodeFromArray[Seq, Short]()
    checkDecodeFromArray[Seq, Int]()
    checkDecodeFromArray[Seq, Long]()
    checkDecodeFromArray[Seq, Float]()
    checkDecodeFromArray[Seq, Double]()
    checkDecodeFromArray[Seq, Boolean]()
    checkDecodeFromArray[Seq, BigDecimal]()
    checkDecodeFromArray[Seq, LocalTime]()
    checkDecodeFromArray[Seq, LocalDate]()
    checkDecodeFromArray[Seq, LocalDateTime]()
    checkDecodeFromArray[Seq, OffsetTime]()
    checkDecodeFromArray[Seq, OffsetDateTime]()
    checkDecodeFromArray[Seq, ZonedDateTime]()
    checkDecodeFromArray[Seq, Instant]()
    checkDecodeFromArray[Seq, UUID]()

    checkDecodeFromSet[String]()
    checkDecodeFromSet[Byte]()
    checkDecodeFromSet[Short]()
    checkDecodeFromSet[Int]()
    checkDecodeFromSet[Long]()
    checkDecodeFromSet[Float]()
    checkDecodeFromSet[Double]()
    checkDecodeFromSet[Boolean]()
    checkDecodeFromSet[BigDecimal]()
    checkDecodeFromSet[LocalTime]()
    checkDecodeFromSet[LocalDate]()
    checkDecodeFromSet[LocalDateTime]()
    checkDecodeFromSet[OffsetTime]()
    checkDecodeFromSet[OffsetDateTime]()
    checkDecodeFromSet[ZonedDateTime]()
    checkDecodeFromSet[Instant]()
    checkDecodeFromSet[UUID]()
  }
   */

  def checkDecodeFromString[T: Arbitrary](implicit
    converter: EndpointConverter[String, T],
    classTag:  ClassTag[T]
  ): Assertion =
    withClue(s"Test for ${ classTag.runtimeClass.getName }") {
      check((v: T) => {
        val decoded = converter.decode(v.toString) match {
          case Success(value) => value
          case unexpected     => fail(s"Value $v got decoded to unexpected $unexpected")
        }
        decoded === v
      })
    }

  def checkDecodeFromArray[F[_], T: Arbitrary]()(implicit
    converter: EndpointConverter[String, F[T]],
    classTag:  ClassTag[F[T]]
  ): Assertion = {
    val gen  = Gen.containerOf[Iterable, T](Arbitrary.arbitrary[T])
    val list = gen.sample.getOrElse(Iterable.empty[T])
    converter.decode(list.iterator.mkString(",")) match {
      case Success(value) =>
        val decoded = value.asInstanceOf[Iterable[T]]
        assert(
          classTag.runtimeClass.isInstance(decoded) &&
            classTag.runtimeClass.isInstance(list) &&
            decoded.size === list.size &&
            decoded === list &&
            decoded.iterator.mkString(",") === list.iterator.mkString(",")
        )
      case InvalidValue(value, _) => assert(value === list.iterator.mkString(","))
      case unexpected => fail(s"Value ${ list.iterator.mkString(",") } got decoded to unexpected $unexpected")
    }
  }

  def checkDecodeFromSet[T: Arbitrary]()(implicit
    converter: EndpointConverter[String, Set[T]],
    classTag:  ClassTag[Set[T]]
  ): Assertion = {
    val gen  = Gen.containerOf[Set, T](Arbitrary.arbitrary[T])
    val list = gen.sample.getOrElse(Set.empty[T])
    converter.decode(list.mkString(",")) match {
      case Success(decoded) =>
        assert(
          classTag.runtimeClass.isInstance(decoded) &&
            classTag.runtimeClass.isInstance(list) &&
            decoded.size === list.size &&
            decoded === list &&
            decoded.iterator.mkString(",") === list.iterator.mkString(",")
        )
      case InvalidValue(value, _) => assert(value === list.iterator.mkString(","))
      case unexpected => fail(s"Value ${ list.iterator.mkString(",") } got decoded to unexpected $unexpected")
    }
  }
}
