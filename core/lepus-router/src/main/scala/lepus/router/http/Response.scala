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
  status:              ResponseStatus,
  headers:             List[Response.CustomHeader[_]] = List.empty,
  description:         String
)(implicit val schema: Schema[T])

object Response {

  def build[T: Encoder: Schema](
    status:      ResponseStatus,
    headers:     List[Response.CustomHeader[_]],
    description: String
  ): Response[T] =
    Response[T](
      status      = status,
      headers     = headers,
      description = description
    )

  case class CustomHeader[T](
    name:        String,
    description: String
  )(implicit val schema: Schema[T])
}
