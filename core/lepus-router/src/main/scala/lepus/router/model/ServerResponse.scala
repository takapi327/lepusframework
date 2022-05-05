/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.model

import fs2.Stream

import org.http4s.{ Response, Uri, Headers => Http4sHeaders }
import org.http4s.headers.Location

import lepus.router.http.ResponseStatus
import lepus.router.http.Header
import lepus.router.http.Header.ResponseHeader
import lepus.router.mvc.ConvertResult

/** A model of the response to be returned in response to a received request.
  *
  * @param status
  *   Status of Response
  * @param headers
  *   An array of headers to be attached to the response.
  * @param body
  *   The value of the response body, converted to a Stream.
  */
case class ServerResponse(
  status:  ResponseStatus,
  headers: Seq[Header],
  body:    Option[ConvertResult]
) {
  def addHeader(header: Header): ServerResponse =
    copy(headers = headers :+ header)
  def addHeaders(headerList: Seq[Header]): ServerResponse =
    copy(headers = headers ++ headerList)

  def toHttp4sResponse[F[_]](): Response[F] = {
    Response[F](
      status  = status.toHttp4sStatus(),
      headers = Http4sHeaders(headers.map(_.toHttp4sHeader()), headers.flatMap(_.uri).map(Location(_))),
      body    = body.map(_.toStream()).getOrElse(Stream.empty)
    )
  }
}

object ServerResponse {

  final class Result(status: ResponseStatus) {
    def apply[C <: ConvertResult](content: C): ServerResponse =
      ServerResponse(status, Seq.empty, Some(content))

    def apply(content: String): ServerResponse =
      ServerResponse(status, Seq(ResponseHeader.TextPlain), Some(ConvertResult.PlainText(content)))
  }

  final class Redirect(status: ResponseStatus) {
    def apply(url: String): ServerResponse =
      Uri.fromString(url) match {
        case Right(uri) => ServerResponse(status, Seq(ResponseHeader("location", uri.renderString, Some(uri))), None)
        case Left(ex)   => throw new Exception(ex.message)
      }

    def apply(url: String, queryParams: Map[String, Seq[String]] = Map.empty): ServerResponse =
      Uri.fromString(bindUrlAndQueryParams(url, queryParams)) match {
        case Right(uri) => ServerResponse(status, Seq(ResponseHeader("location", uri.renderString, Some(uri))), None)
        case Left(ex)   => throw new Exception(ex.message)
      }
  }

  /** Process for linking URLs to query parameters.
    *
    * @param url
    *   String of the URL to redirect to.
    * @param queryParams
    *   Query parameter to pass to the redirect URL.
    * @return
    *   The complete path with the URL to be redirected to and the query parameters tied to it.
    */
  private[lepus] def bindUrlAndQueryParams(url: String, queryParams: Map[String, Seq[String]]): String =
    if (queryParams.isEmpty) url
    else {
      val queryString: String = queryParams
        .map {
          case (key, values) => s"$key=${ values.mkString(",") }"
        }
        .mkString("&")
      url + (if (url.contains("?")) "&" else "?") + queryString
    }

  lazy val Result   = (status: ResponseStatus) => new Result(status)
  lazy val Redirect = (status: ResponseStatus) => new Redirect(status)

  /** Generates a ‘200 OK’ result. */
  val Ok = Result(ResponseStatus.Ok)

  /** Generates a ‘201 CREATED’ result. */
  val Created = Result(ResponseStatus.Created)

  /** Generates a ‘202 ACCEPTED’ result. */
  val Accepted = Result(ResponseStatus.Accepted)

  /** Generates a ‘203 NON_AUTHORITATIVE_INFORMATION’ result. */
  val NonAuthoritativeInformation = Result(ResponseStatus.NonAuthoritativeInformation)

  /** Generates a ‘204 NO_CONTENT’ result. */
  val NoContent = ServerResponse(ResponseStatus.NoContent, Seq.empty, None)

  /** Generates a ‘205 RESET_CONTENT’ result. */
  val ResetContent = ServerResponse(ResponseStatus.ResetContent, Seq.empty, None)

  /** Generates a ‘206 PARTIAL_CONTENT’ result. */
  val PartialContent = Result(ResponseStatus.PartialContent)

  /** Generates a ‘207 MULTI_STATUS’ result. */
  val MultiStatus = Result(ResponseStatus.MultiStatus)

  /** Generates a ‘300 MULTIPLE_CHOICES’ simple result. */
  val MultipleChoices: Redirect = Redirect(ResponseStatus.MultipleChoices)

  /** Generates a ‘301 MOVED_PERMANENTLY’ simple result. */
  val MovedPermanently: Redirect = Redirect(ResponseStatus.MovedPermanently)

  /** Generates a ‘302 FOUND’ simple result. */
  val Found: Redirect = Redirect(ResponseStatus.Found)

  /** Generates a ‘303 SEE_OTHER’ simple result. */
  val SeeOther: Redirect = Redirect(ResponseStatus.SeeOther)

  /** Generates a ‘304 NOT_MODIFIED’ result. */
  val NotModified: Redirect = Redirect(ResponseStatus.NotModified)

  /** Generates a ‘307 TEMPORARY_REDIRECT’ simple result. */
  val TemporaryRedirect: Redirect = Redirect(ResponseStatus.TemporaryRedirect)

  /** Generates a ‘308 PERMANENT_REDIRECT’ simple result. */
  val PermanentRedirect: Redirect = Redirect(ResponseStatus.PermanentRedirect)

  /** Generates a ‘400 BAD_REQUEST’ result. */
  val BadRequest = Result(ResponseStatus.BadRequest)

  /** Generates a ‘401 UNAUTHORIZED’ result. */
  val Unauthorized = Result(ResponseStatus.Unauthorized)

  /** Generates a ‘402 PAYMENT_REQUIRED’ result. */
  val PaymentRequired = Result(ResponseStatus.PaymentRequired)

  /** Generates a ‘403 FORBIDDEN’ result. */
  val Forbidden = Result(ResponseStatus.Forbidden)

  /** Generates a ‘404 NOT_FOUND’ result. */
  val NotFound = Result(ResponseStatus.NotFound)

  /** Generates a ‘405 METHOD_NOT_ALLOWED’ result. */
  val MethodNotAllowed = Result(ResponseStatus.MethodNotAllowed)

  /** Generates a ‘406 NOT_ACCEPTABLE’ result. */
  val NotAcceptable = Result(ResponseStatus.NotAcceptable)

  /** Generates a ‘408 REQUEST_TIMEOUT’ result. */
  val RequestTimeout = Result(ResponseStatus.RequestTimeout)

  /** Generates a ‘409 CONFLICT’ result. */
  val Conflict = Result(ResponseStatus.Conflict)

  /** Generates a ‘410 GONE’ result. */
  val Gone = Result(ResponseStatus.Gone)

  /** Generates a ‘412 PRECONDITION_FAILED’ result. */
  val PreconditionFailed = Result(ResponseStatus.PreconditionFailed)

  /** Generates a ‘413 REQUEST_ENTITY_TOO_LARGE’ result. */
  val EntityTooLarge = Result(ResponseStatus.RequestEntityTooLarge)

  /** Generates a ‘414 REQUEST_URI_TOO_LONG’ result. */
  val UriTooLong = Result(ResponseStatus.RequestUriTooLong)

  /** Generates a ‘415 UNSUPPORTED_MEDIA_TYPE’ result. */
  val UnsupportedMediaType = Result(ResponseStatus.UnsupportedMediaType)

  /** Generates a ‘417 EXPECTATION_FAILED’ result. */
  val ExpectationFailed = Result(ResponseStatus.ExpectationFailed)

  /** Generates a ‘418 IM_A_TEAPOT’ result. */
  val ImATeapot = Result(ResponseStatus.ImATeapot)

  /** Generates a ‘422 UNPROCESSABLE_ENTITY’ result. */
  val UnprocessableEntity = Result(ResponseStatus.UnprocessableEntity)

  /** Generates a ‘423 LOCKED’ result. */
  val Locked = Result(ResponseStatus.Locked)

  /** Generates a ‘424 FAILED_DEPENDENCY’ result. */
  val FailedDependency = Result(ResponseStatus.FailedDependency)

  /** Generates a ‘428 PRECONDITION_REQUIRED’ result. */
  val PreconditionRequired = Result(ResponseStatus.PreconditionRequired)

  /** Generates a ‘429 TOO_MANY_REQUESTS’ result. */
  val TooManyRequests = Result(ResponseStatus.TooManyRequests)

  /** Generates a ‘431 REQUEST_HEADER_FIELDS_TOO_LARGE’ result. */
  val RequestHeaderFieldsTooLarge = Result(ResponseStatus.RequestHeaderFieldsTooLarge)

  /** Generates a ‘500 INTERNAL_SERVER_ERROR’ result. */
  val InternalServerError = Result(ResponseStatus.InternalServerError)

  /** Generates a ‘501 NOT_IMPLEMENTED’ result. */
  val NotImplemented = Result(ResponseStatus.NotImplemented)

  /** Generates a ‘502 BAD_GATEWAY’ result. */
  val BadGateway = Result(ResponseStatus.BadGateway)

  /** Generates a ‘503 SERVICE_UNAVAILABLE’ result. */
  val ServiceUnavailable = Result(ResponseStatus.ServiceUnavailable)

  /** Generates a ‘504 GATEWAY_TIMEOUT’ result. */
  val GatewayTimeout = Result(ResponseStatus.GatewayTimeout)

  /** Generates a ‘505 HTTP_VERSION_NOT_SUPPORTED’ result. */
  val HttpVersionNotSupported = Result(ResponseStatus.HttpVersionNotSupported)

  /** Generates a ‘507 INSUFFICIENT_STORAGE’ result. */
  val InsufficientStorage = Result(ResponseStatus.InsufficientStorage)

  /** Generates a ‘511 NETWORK_AUTHENTICATION_REQUIRED’ result. */
  val NetworkAuthenticationRequired = Result(ResponseStatus.NetworkAuthenticationRequired)
}
