/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.model

sealed trait DecodeRequestResult
object DecodeRequestResult {
  case class Success(response: Vector[Any]) extends DecodeRequestResult
  case class Failure() extends DecodeRequestResult
}
