/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import io.circe.Encoder

import lepus.router.model.Schema

/** API Response Value
  *
  * @param status
  *   Response Status Code
  * @param headers
  *   List of headers given to the response
  * @param description
  *   Response Description
  */
case class Response[T: Encoder](
  status:           Status,
  headers:          List[Header.CustomHeader[?]] = List.empty,
  description:      String
)(using val schema: Schema[T])

object Response:

  def build[T: Encoder: Schema](
    status:      Status,
    headers:     List[Header.CustomHeader[?]],
    description: String
  ): Response[T] =
    Response[T](
      status      = status,
      headers     = headers,
      description = description
    )
end Response
