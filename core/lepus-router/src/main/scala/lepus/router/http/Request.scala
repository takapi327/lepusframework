/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import org.http4s.{ Request => Http4sRequest, Uri }

trait HttpRequest:
  def method:          Method
  def pathSegments:    List[String]
  def queryParameters: Map[String, Seq[String]]

class Request[F[_]](request: Http4sRequest[F]) extends HttpRequest:
  lazy val method: Method = request.method.name.toUpperCase match {
    case "GET"     => Method.Get
    case "HEAD"    => Method.Head
    case "POST"    => Method.Post
    case "PUT"     => Method.Put
    case "DELETE"  => Method.Delete
    case "OPTIONS" => Method.Options
    case "PATCH"   => Method.Patch
    case "CONNECT" => Method.Connect
    case "TRACE"   => Method.Trace
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
