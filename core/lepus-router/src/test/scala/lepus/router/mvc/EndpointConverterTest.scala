/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.mvc

import java.time._
import java.util._

import scala.reflect.ClassTag

import org.scalacheck.Arbitrary
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

  def checkDecodeFromString[T: Arbitrary](implicit converter: EndpointConverter[String, T], classTag: ClassTag[T]): Assertion =
    withClue(s"Test for ${classTag.runtimeClass.getName}") {
      check((v: T) => {
        val decoded = converter.decode(v.toString) match {
          case Success(value) => value
          case unexpected     => fail(s"Value $v got decoded to unexpected $unexpected")
        }
        decoded === v
      })
    }
}
