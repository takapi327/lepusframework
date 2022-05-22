/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

/** API Response Value
  *
  * @param status
  *   Response Status Code
  * @param headers
  *   List of headers given to the response
  * @param description
  *   Response Description
  */
case class Response[T: io.circe.Encoder](
  status:              ResponseStatus,
  headers:             List[Response.CustomHeader] = List.empty,
  description:         String
)(implicit val schema: lepus.router.model.Schema[T])

object Response {

  def build[T: io.circe.Encoder: lepus.router.model.Schema](
    status:      ResponseStatus,
    headers:     List[Response.CustomHeader],
    description: String
  ): Response[T] =
    Response[T](
      status      = status,
      headers     = headers,
      description = description
    )

  case class CustomHeader(
    name:        String,
    schema:      Schema,
    description: String
  )

  case class Schema(
    `type`: String,
    format: Option[String] = None
  )
}
