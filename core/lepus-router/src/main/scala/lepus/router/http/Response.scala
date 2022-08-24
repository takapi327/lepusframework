/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import fs2.Stream

import org.http4s.*
import org.http4s.headers.*
import org.http4s.{ Response as Http4sResponse, Header as Http4sHeader }

import lepus.router.ConvertResult

/** A model of the response to be returned in response to a received request.
  *
  * @param status
  *   Status of Response
  * @param headers
  *   An array of headers to be attached to the response.
  * @param body
  *   The value of the response body, converted to a Stream.
  */
case class Response(
  status:  Status,
  headers: Headers,
  body:    Option[ConvertResult]
):
  def addHeader[H: [A] =>> Http4sHeader[A, Http4sHeader.Recurring]](h: H): Response =
    copy(headers = this.headers put Http4sHeader.ToRaw.modelledHeadersToRaw(h).values)

  /** Add a [[org.http4s.headers.`Set-Cookie`]] header with the provided values */
  def addCookie(cookie: ResponseCookie): Response =
    copy(headers = this.headers add `Set-Cookie`(cookie))

  /** Add a [[org.http4s.headers.`Set-Cookie`]] which will remove the specified cookie from the client
    */
  def addCookie(name: String, content: String, expires: Option[HttpDate] = None): Response =
    addCookie(ResponseCookie(name, content, expires))

  /** Add a [[org.http4s.headers.`Set-Cookie`]] which will remove the specified cookie from the client */
  def removeCookie(name: String): Response =
    addCookie(ResponseCookie(name, "").clearCookie)

  def toHttp4sResponse[F[_]](): Http4sResponse[F] =
    Http4sResponse[F](
      status  = status,
      headers = headers,
      body    = body.map(_.toStream()).getOrElse(Stream.empty)
    )

object Response:

  final class Result(status: Status):
    def apply[C <: ConvertResult](content: C): Response =
      val header = content match
        case _: ConvertResult.JsValue[?]   => `Content-Type`(MediaType.application.json)
        case _: ConvertResult.PlainText[?] => `Content-Type`(MediaType.text.plain)
        case _ => throw new IllegalArgumentException("The value received will not match any of the ConvertResult.")
      Response(status, Headers(header), Some(content))

    def apply(content: String): Response =
      Response(status, Headers(`Content-Type`(MediaType.text.plain)), Some(ConvertResult.PlainText(content)))

  final class Redirect(status: Status):
    def apply(url: String): Response =
      Uri.fromString(url) match
        case Right(uri) =>
          Response(status, Headers(Location(uri)), None)
        case Left(ex) => throw new Exception(ex.message)

    def apply(url: String, queryParams: Map[String, Seq[String]] = Map.empty): Response =
      Uri.fromString(bindUrlAndQueryParams(url, queryParams)) match
        case Right(uri) =>
          Response(status, Headers(Location(uri)), None)
        case Left(ex) => throw new Exception(ex.message)

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
    if queryParams.isEmpty then url
    else
      val queryString: String = queryParams
        .map {
          case (key, values) => s"$key=${ values.mkString(",") }"
        }
        .mkString("&")
      url + (if (url.contains("?")) "&" else "?") + queryString

  lazy val Result   = (status: Status) => new Result(status)
  lazy val Redirect = (status: Status) => new Redirect(status)

  /** Generates a ‘200 OK’ result. */
  val Ok = Result(Status.Ok)

  /** Generates a ‘201 CREATED’ result. */
  val Created = Result(Status.Created)

  /** Generates a ‘202 ACCEPTED’ result. */
  val Accepted = Result(Status.Accepted)

  /** Generates a ‘203 NON_AUTHORITATIVE_INFORMATION’ result. */
  val NonAuthoritativeInformation = Result(Status.NonAuthoritativeInformation)

  /** Generates a ‘204 NO_CONTENT’ result. */
  val NoContent = Response(Status.NoContent, Headers.empty, None)

  /** Generates a ‘205 RESET_CONTENT’ result. */
  val ResetContent = Response(Status.ResetContent, Headers.empty, None)

  /** Generates a ‘206 PARTIAL_CONTENT’ result. */
  val PartialContent = Result(Status.PartialContent)

  /** Generates a ‘207 MULTI_STATUS’ result. */
  val MultiStatus = Result(Status.MultiStatus)

  /** Generates a ‘300 MULTIPLE_CHOICES’ simple result. */
  val MultipleChoices: Redirect = Redirect(Status.MultipleChoices)

  /** Generates a ‘301 MOVED_PERMANENTLY’ simple result. */
  val MovedPermanently: Redirect = Redirect(Status.MovedPermanently)

  /** Generates a ‘302 FOUND’ simple result. */
  val Found: Redirect = Redirect(Status.Found)

  /** Generates a ‘303 SEE_OTHER’ simple result. */
  val SeeOther: Redirect = Redirect(Status.SeeOther)

  /** Generates a ‘304 NOT_MODIFIED’ result. */
  val NotModified: Redirect = Redirect(Status.NotModified)

  /** Generates a ‘307 TEMPORARY_REDIRECT’ simple result. */
  val TemporaryRedirect: Redirect = Redirect(Status.TemporaryRedirect)

  /** Generates a ‘308 PERMANENT_REDIRECT’ simple result. */
  val PermanentRedirect: Redirect = Redirect(Status.PermanentRedirect)

  /** Generates a ‘400 BAD_REQUEST’ result. */
  val BadRequest = Result(Status.BadRequest)

  /** Generates a ‘401 UNAUTHORIZED’ result. */
  val Unauthorized = Result(Status.Unauthorized)

  /** Generates a ‘402 PAYMENT_REQUIRED’ result. */
  val PaymentRequired = Result(Status.PaymentRequired)

  /** Generates a ‘403 FORBIDDEN’ result. */
  val Forbidden = Result(Status.Forbidden)

  /** Generates a ‘404 NOT_FOUND’ result. */
  val NotFound = Result(Status.NotFound)

  /** Generates a ‘405 METHOD_NOT_ALLOWED’ result. */
  val MethodNotAllowed = Result(Status.MethodNotAllowed)

  /** Generates a ‘406 NOT_ACCEPTABLE’ result. */
  val NotAcceptable = Result(Status.NotAcceptable)

  /** Generates a ‘408 REQUEST_TIMEOUT’ result. */
  val RequestTimeout = Result(Status.RequestTimeout)

  /** Generates a ‘409 CONFLICT’ result. */
  val Conflict = Result(Status.Conflict)

  /** Generates a ‘410 GONE’ result. */
  val Gone = Result(Status.Gone)

  /** Generates a ‘412 PRECONDITION_FAILED’ result. */
  val PreconditionFailed = Result(Status.PreconditionFailed)

  /** Generates a ‘413 REQUEST_ENTITY_TOO_LARGE’ result. */
  val EntityTooLarge = Result(Status.PayloadTooLarge)

  /** Generates a ‘414 REQUEST_URI_TOO_LONG’ result. */
  val UriTooLong = Result(Status.UriTooLong)

  /** Generates a ‘415 UNSUPPORTED_MEDIA_TYPE’ result. */
  val UnsupportedMediaType = Result(Status.UnsupportedMediaType)

  /** Generates a ‘417 EXPECTATION_FAILED’ result. */
  val ExpectationFailed = Result(Status.ExpectationFailed)

  /** Generates a ‘418 IM_A_TEAPOT’ result. */
  val ImATeapot = Result(Status.ImATeapot)

  /** Generates a ‘422 UNPROCESSABLE_ENTITY’ result. */
  val UnprocessableEntity = Result(Status.UnprocessableEntity)

  /** Generates a ‘423 LOCKED’ result. */
  val Locked = Result(Status.Locked)

  /** Generates a ‘424 FAILED_DEPENDENCY’ result. */
  val FailedDependency = Result(Status.FailedDependency)

  /** Generates a ‘428 PRECONDITION_REQUIRED’ result. */
  val PreconditionRequired = Result(Status.PreconditionRequired)

  /** Generates a ‘429 TOO_MANY_REQUESTS’ result. */
  val TooManyRequests = Result(Status.TooManyRequests)

  /** Generates a ‘431 REQUEST_HEADER_FIELDS_TOO_LARGE’ result. */
  val RequestHeaderFieldsTooLarge = Result(Status.RequestHeaderFieldsTooLarge)

  /** Generates a ‘500 INTERNAL_SERVER_ERROR’ result. */
  val InternalServerError = Result(Status.InternalServerError)

  /** Generates a ‘501 NOT_IMPLEMENTED’ result. */
  val NotImplemented = Result(Status.NotImplemented)

  /** Generates a ‘502 BAD_GATEWAY’ result. */
  val BadGateway = Result(Status.BadGateway)

  /** Generates a ‘503 SERVICE_UNAVAILABLE’ result. */
  val ServiceUnavailable = Result(Status.ServiceUnavailable)

  /** Generates a ‘504 GATEWAY_TIMEOUT’ result. */
  val GatewayTimeout = Result(Status.GatewayTimeout)

  /** Generates a ‘505 HTTP_VERSION_NOT_SUPPORTED’ result. */
  val HttpVersionNotSupported = Result(Status.HttpVersionNotSupported)

  /** Generates a ‘507 INSUFFICIENT_STORAGE’ result. */
  val InsufficientStorage = Result(Status.InsufficientStorage)

  /** Generates a ‘511 NETWORK_AUTHENTICATION_REQUIRED’ result. */
  val NetworkAuthenticationRequired = Result(Status.NetworkAuthenticationRequired)
