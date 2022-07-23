/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.model

import org.http4s.Request

import lepus.router.http.Header

class ServerRequest[F[_], T](request: Request[F], val param: T):

  lazy val protocol: String = request.httpVersion.toString()

  lazy val headers: Seq[Header] =
    request.headers.headers.map(header => Header(header.name.toString, header.value))

  lazy val contentType:   Option[String] = findHeaderValue(Header.CONTENT_TYPE)
  lazy val contentLength: Option[Long]   = findHeaderValue(Header.CONTENT_LENGTH).flatMap(_.toLongOption)

  /** Based on the name of the header, get the value associated with it.
    * @param name
    *   Http Request Header name
    * @return
    *   Http Request Header value
    */
  def findHeaderValue(name: String): Option[String] = headers.find(_.is(name)).map(_.value)

  def as[A](using F: cats.MonadThrow[F], decoder: org.http4s.EntityDecoder[F, A]): F[A] =
    request.as[A]
