/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router

import org.http4s._

import http._

class ServerRequest[F[_]](request: Request[F]) {
  lazy val protocol: String = request.httpVersion.toString()
  lazy val pathSegments: List[String] = {
    request.pathInfo.renderString
      .dropWhile(_ == '/').split("/")
      .toList.map(Uri.decode(_))
  }
  lazy val method: String = request.method.name.toUpperCase
  lazy val headers: Seq[RequestHeader] = request.headers.headers.map(
    header => RequestHeader(header.name.toString, header.value)
  )

  lazy val contentType:   Option[String] = findHeaderValue(RequestHeader.ContentType)
  lazy val contentLength: Option[Long]   = findHeaderValue(RequestHeader.ContentLength).flatMap(_.toLongOption)

  /**
   * Based on the name of the header, get the value associated with it.
   * @param name Http Request Header name
   * @return Http Request Header value
   */
  def findHeaderValue(name: String): Option[String] = headers.find(_.is(name)).map(_.value)
}
