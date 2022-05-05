/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.model

sealed trait DecodeResult[+T]
object DecodeResult {
  case class Success[T](value: T) extends DecodeResult[T]

  sealed trait Failure                                                 extends DecodeResult[Nothing]
  case class InvalidValue(value: String, throwable: Option[Throwable]) extends Failure
  case class Mismatch(request: String, endpoint: String)               extends Failure
  case object Missing                                                  extends Failure
}
