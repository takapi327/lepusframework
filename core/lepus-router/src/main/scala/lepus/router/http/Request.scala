/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import org.http4s.{ Request => Http4sRequest, Uri }

trait HttpRequest {
  def method:          RequestMethod
  def pathSegments:    List[String]
  def queryParameters: Map[String, Seq[String]]
}

class Request[F[_]](request: Http4sRequest[F]) extends HttpRequest {
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

  lazy val pathSegments: List[String] = {
    request.pathInfo.renderString
      .dropWhile(_ == '/')
      .split("/")
      .toList
      .map(Uri.decode(_))
  }

  lazy val queryParameters: Map[String, Seq[String]] = request.multiParams
}
