/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import lepus.router.model.Schema

case class RequestBody[T](
  description:         String
)(implicit val schema: Schema[T])

object RequestBody {

  def build[T: Schema](
    description: String
  ): RequestBody[T] =
    RequestBody(description)
}
