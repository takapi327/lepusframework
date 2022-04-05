/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.model

import fs2._

import org.http4s.{ Response, Headers => Http4sHeaders }

import lepus.router.http.ResponseStatus
import lepus.router.http.Header.ResponseHeader
import lepus.router.mvc.ConvertResult

case class ServerResponse(
  status:  ResponseStatus,
  headers: Seq[ResponseHeader],
  body:    Option[ConvertResult]
) {
  def addHeader(header: ResponseHeader): ServerResponse =
    copy(headers = headers :+ header)
  def addHeaders(headers: Seq[ResponseHeader]): ServerResponse =
    copy(headers = headers ++ headers)

  def toHttp4sResponse[F[_]](): Response[F] = {
    Response[F](
      status  = status.toHttp4sStatus(),
      headers = Http4sHeaders(headers.map(_.toHttp4sHeader())),
      body    = body.map(_.toStream()).getOrElse(Stream.empty)
    )
  }
}

object ServerResponse {

  class Result(status: ResponseStatus) extends ServerResponse(status, Seq.empty, None) {
    def apply[C <: ConvertResult](content: C): ServerResponse = {
      val defaultHeader = content match {
        case ConvertResult.JsValue(_) => ResponseHeader.ApplicationJson
      }
      ServerResponse(status, Seq(defaultHeader), Some(content))
    }
  }

  /** Generates a ‘200 OK’ result. */
  val Ok = new Result(ResponseStatus.Ok)

  /** Generates a ‘201 CREATED’ result. */
  val Created = new Result(ResponseStatus.Created)

  /** Generates a ‘202 ACCEPTED’ result. */
  val Accepted = new Result(ResponseStatus.Accepted)

  /** Generates a ‘203 NON_AUTHORITATIVE_INFORMATION’ result. */
  val NonAuthoritativeInformation = new Result(ResponseStatus.NonAuthoritativeInformation)

  /** Generates a ‘204 NO_CONTENT’ result. */
  val NoContent = ServerResponse(ResponseStatus.NoContent, Seq.empty, None)

  /** Generates a ‘205 RESET_CONTENT’ result. */
  val ResetContent = ServerResponse(ResponseStatus.ResetContent, Seq.empty, None)

  /** Generates a ‘206 PARTIAL_CONTENT’ result. */
  val PartialContent = new Result(ResponseStatus.PartialContent)

  /** Generates a ‘207 MULTI_STATUS’ result. */
  val MultiStatus = new Result(ResponseStatus.MultiStatus)

}
