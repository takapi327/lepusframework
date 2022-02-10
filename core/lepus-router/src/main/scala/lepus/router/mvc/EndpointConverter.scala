/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.mvc

import lepus.router.model.DecodeResult

import scala.annotation._
import scala.util.{Failure, Success, Try}

@implicitNotFound("")
trait EndpointConverter[L, H] {

  def stringToResult(from: L): DecodeResult[H]

  def decode(str: L): DecodeResult[H] = decode(str, stringToResult)

  def decode(str: L, fromTo: L => DecodeResult[H]): DecodeResult[H] = {
    Try { fromTo(str) } match {
      case Success(s) => s
      case Failure(e) => DecodeResult.InvalidValue(str.toString, e)
    }
  }
}

object EndpointConverter {

  implicit val string: EndpointConverter[String, String] = new EndpointConverter[String, String] {
    override def stringToResult(str: String): DecodeResult[String] = DecodeResult.Success(str)
  }
  implicit val long: EndpointConverter[String, Long] = new EndpointConverter[String, Long] {
    override def stringToResult(str: String): DecodeResult[Long] = DecodeResult.Success(str.toLong)
  }
  implicit val int: EndpointConverter[String, Int] = new EndpointConverter[String, Int] {
    override def stringToResult(str: String): DecodeResult[Int] = DecodeResult.Success(str.toInt)
  }

  def stringTo[T](fromTo: String => T): String => DecodeResult[T] =
    fromTo.andThen(DecodeResult.Success(_))
}
