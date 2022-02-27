/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.model

import org.http4s.{ Request, Uri }

import lepus.router.http.{ RequestMethod, Header}

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
  lazy val method: RequestMethod = request.method.name.toUpperCase match {
    case "GET"     => RequestMethod.Get
    case "HEAD"    => RequestMethod.Head
    case "POST"    => RequestMethod.Post
    case "PUT"     => RequestMethod.Put
    case "DELETE"  => RequestMethod.Delete
    case "OPTIONS" => RequestMethod.Options
    case "PATCH"   => RequestMethod.Patch
    case "CONNECT" => RequestMethod.Connect
    case "TRACE"   => RequestMethod.Trace
    case _         => throw new NoSuchElementException("The request method received did not match the expected value.")
  }
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
