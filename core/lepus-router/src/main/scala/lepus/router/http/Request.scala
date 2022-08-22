/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import scala.annotation.targetName

import cats.MonadThrow

import org.http4s.{ Request as Http4sRequest, Uri, EntityDecoder }

import lepus.router.model.Schema

trait HttpRequest:
  def method:                         Method
  private[lepus] def pathSegments:    List[String]
  private[lepus] def queryParameters: Map[String, Seq[String]]

class Request[F[_]](request: Http4sRequest[F]) extends HttpRequest:
  val method: Method = request.method.name.toUpperCase match
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

  private[lepus] val pathSegments: List[String] =
    request.pathInfo.renderString
      .dropWhile(_ == '/')
      .split("/")
      .toList
      .map(Uri.decode(_))

  private[lepus] val queryParameters: Map[String, Seq[String]] = request.multiParams

  opaque type Protocol = String
  extension (prot: Protocol) @targetName("protocolAsString") def asString: String = prot

  opaque type ContentType = String
  extension (content: ContentType) @targetName("contentTypeAsString") def asString: String = content

  opaque type ContentLength = Long
  extension (content: ContentLength) def asLong: Long = content

  val protocol: Protocol = request.httpVersion.toString()

  val headers: Seq[Header] =
    request.headers.headers.flatMap(header => {
      val name = Header.FieldName.values.find(v => v.name == header.name.toString)
      name.map(Header(_, header.value))
    })

  val contentType: Option[ContentType] = findHeaderValue(Header.FieldName.ContentType)
  val contentLength: Option[ContentLength] =
    findHeaderValue(Header.FieldName.ContentLength).flatMap(_.toLongOption)

  /** Based on the name of the header, get the value associated with it.
    * @param name
    *   Http Request Header name
    * @return
    *   Http Request Header value
    */
  def findHeaderValue(name: Header.FieldName): Option[ContentType] = headers.find(_.is(name)).map(_.getValue)

  /** Information contained in the body of an Http request is converted to an arbitrary type and acquired.
    *
    * @tparam A
    *   A type of the result
    * @return
    *   the effect which will generate the A
    */
  def body[A](using MonadThrow[F], EntityDecoder[F, A]): F[A] =
    request.as[A]

object Request:

  case class Body[T](
    description:      String
  )(using val schema: Schema[T])

  object Body:

    def build[T: Schema](
      description: String
    ): Body[T] = Body(description)
  end Body

end Request
