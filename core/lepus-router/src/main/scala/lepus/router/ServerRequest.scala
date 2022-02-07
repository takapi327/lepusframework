/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router

import org.http4s.{ Request, Uri }

import http._

trait HttpServerRequest {
  def pathSegments: List[String]
}

class ServerRequest[F[_]](request: Request[F]) extends HttpServerRequest {
  lazy val protocol: String = request.httpVersion.toString()
  lazy val pathSegments: List[String] = {
    request.pathInfo.renderString
      .dropWhile(_ == '/').split("/")
      .toList.map(Uri.decode(_))
  }
  lazy val method: String = request.method.name.toUpperCase
  lazy val headers: Seq[Header.RequestHeader] = request.headers.headers.map(
    header => Header.RequestHeader(header.name.toString, header.value)
  )

  lazy val contentType:   Option[String] = findHeaderValue(Header.CONTENT_TYPE)
  lazy val contentLength: Option[Long]   = findHeaderValue(Header.CONTENT_LENGTH).flatMap(_.toLongOption)

  /**
   * Based on the name of the header, get the value associated with it.
   * @param name Http Request Header name
   * @return Http Request Header value
   */
  def findHeaderValue(name: String): Option[String] = headers.find(_.is(name)).map(_.value)
}
