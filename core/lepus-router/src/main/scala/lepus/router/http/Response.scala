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
  * @param content
  *   Response Body Value　
  */
case class Response(
  status:      ResponseStatus,
  headers:     List[Response.CustomHeader] = List.empty,
  description: String,
  content:     List[Response.Content]      = List.empty
)

object Response {

  case class CustomHeader(
    name:        String,
    schema:      Schema,
    description: String
  )

  case class Content(
    mediaType: Header.ResponseHeader,
    schema:    String
  )

  case class Schema(
    `type`: String,
    format: Option[String] = None
  )
}
