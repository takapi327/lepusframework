/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.http

import scala.annotation._

@implicitNotFound("")
trait EndpointConverter[L, H] {

  def decode(low:  L): H
  def encode(high: H): L
}

object EndpointConverter {

  implicit val string: EndpointConverter[String, String] = new EndpointConverter[String, String] {
    def decode(low:  String): String = low
    def encode(high: String): String = high
  }

  implicit val long: EndpointConverter[String, Long] = new EndpointConverter[String, Long] {
    def decode(low:  String): Long   = low.toLong
    def encode(high: Long):   String = high.toString
  }
}
