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

  /** Generates a ‘400 BAD_REQUEST’ result. */
  val BadRequest = new Result(ResponseStatus.BadRequest)

  /** Generates a ‘401 UNAUTHORIZED’ result. */
  val Unauthorized = new Result(ResponseStatus.Unauthorized)

  /** Generates a ‘402 PAYMENT_REQUIRED’ result. */
  val PaymentRequired = new Result(ResponseStatus.PaymentRequired)

  /** Generates a ‘403 FORBIDDEN’ result. */
  val Forbidden = new Result(ResponseStatus.Forbidden)

  /** Generates a ‘404 NOT_FOUND’ result. */
  val NotFound = new Result(ResponseStatus.NotFound)

  /** Generates a ‘405 METHOD_NOT_ALLOWED’ result. */
  val MethodNotAllowed = new Result(ResponseStatus.MethodNotAllowed)

  /** Generates a ‘406 NOT_ACCEPTABLE’ result. */
  val NotAcceptable = new Result(ResponseStatus.NotAcceptable)

  /** Generates a ‘408 REQUEST_TIMEOUT’ result. */
  val RequestTimeout = new Result(ResponseStatus.RequestTimeout)

  /** Generates a ‘409 CONFLICT’ result. */
  val Conflict = new Result(ResponseStatus.Conflict)

  /** Generates a ‘410 GONE’ result. */
  val Gone = new Result(ResponseStatus.Gone)

  /** Generates a ‘412 PRECONDITION_FAILED’ result. */
  val PreconditionFailed = new Result(ResponseStatus.PreconditionFailed)

  /** Generates a ‘413 REQUEST_ENTITY_TOO_LARGE’ result. */
  val EntityTooLarge = new Result(ResponseStatus.RequestEntityTooLarge)

  /** Generates a ‘414 REQUEST_URI_TOO_LONG’ result. */
  val UriTooLong = new Result(ResponseStatus.RequestUriTooLong)

  /** Generates a ‘415 UNSUPPORTED_MEDIA_TYPE’ result. */
  val UnsupportedMediaType = new Result(ResponseStatus.UnsupportedMediaType)

  /** Generates a ‘417 EXPECTATION_FAILED’ result. */
  val ExpectationFailed = new Result(ResponseStatus.ExpectationFailed)

  /** Generates a ‘418 IM_A_TEAPOT’ result. */
  val ImATeapot = new Result(ResponseStatus.ImATeapot)

  /** Generates a ‘422 UNPROCESSABLE_ENTITY’ result. */
  val UnprocessableEntity = new Result(ResponseStatus.UnprocessableEntity)

  /** Generates a ‘423 LOCKED’ result. */
  val Locked = new Result(ResponseStatus.Locked)

  /** Generates a ‘424 FAILED_DEPENDENCY’ result. */
  val FailedDependency = new Result(ResponseStatus.FailedDependency)

  /** Generates a ‘428 PRECONDITION_REQUIRED’ result. */
  val PreconditionRequired = new Result(ResponseStatus.PreconditionRequired)

  /** Generates a ‘429 TOO_MANY_REQUESTS’ result. */
  val TooManyRequests = new Result(ResponseStatus.TooManyRequests)

  /** Generates a ‘431 REQUEST_HEADER_FIELDS_TOO_LARGE’ result. */
  val RequestHeaderFieldsTooLarge = new Result(ResponseStatus.RequestHeaderFieldsTooLarge)
}
