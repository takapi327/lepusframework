/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.model

import scala.annotation.targetName

import cats.MonadThrow

import org.http4s.{ Request, EntityDecoder }

import lepus.router.http.Header

class ServerRequest[F[_]](request: Request[F]):

  opaque type Protocol = String
  extension (prot: Protocol) @targetName("protocolAsString") def asString: String = prot

  opaque type ContentType = String
  extension (content: ContentType) @targetName("contentTypeAsString") def asString: String = content

  opaque type ContentLength = Long
  extension (content: ContentLength) def asLong: Long = content

  lazy val protocol: Protocol = request.httpVersion.toString()

  lazy val headers: Seq[Header] =
    request.headers.headers.map(header => Header(header.name.toString, header.value))

  lazy val contentType:   Option[ContentType]   = findHeaderValue(Header.FieldName.ContentType.name)
  lazy val contentLength: Option[ContentLength] = findHeaderValue(Header.FieldName.ContentLength.name).flatMap(_.toLongOption)

  /** Based on the name of the header, get the value associated with it.
    * @param name
    *   Http Request Header name
    * @return
    *   Http Request Header value
    */
  def findHeaderValue(name: String): Option[ContentType] = headers.find(_.is(name)).map(_.getValue)

  def as[A](using MonadThrow[F], EntityDecoder[F, A]): F[A] =
    request.as[A]
