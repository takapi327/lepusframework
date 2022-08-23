/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import scala.annotation.targetName
import cats.MonadThrow
import org.http4s.{ EntityDecoder, RequestCookie, Uri, Request as Http4sRequest }
import lepus.router.model.Schema

/**
 * Class for Endpoint validation. Generate necessary values from Http4s requests.
 * 
 * @param pathSegments
 *   The value of the URL of the Http request, divided by /.
 * @param queryParameters
 *   Alias for the query parameter of the Http request.
 */
case class Request(
  pathSegments:    List[String],
  queryParameters: Map[String, Seq[String]]
)

object Request:

  def fromHttp4s[F[_]](request: Http4sRequest[F]): Request =
    val pathSegments = request.pathInfo.renderString
      .dropWhile(_ == '/')
      .split("/")
      .toList
      .map(Uri.decode(_))
    val queryParameters = request.multiParams
    Request(pathSegments, queryParameters)

  case class Body[T](
    description:      String
  )(using val schema: Schema[T])

  object Body:

    def build[T: Schema](
      description: String
    ): Body[T] = Body(description)
  end Body

end Request
