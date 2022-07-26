/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.model

import fs2.Stream

import org.http4s.{ Response as Http4sResponse, Uri, Headers as Http4sHeaders }
import org.http4s.headers.Location

import lepus.router.http.{ Response, Header }
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
case class ServerResponse(
  status:  Response.Status,
  headers: Seq[Header],
  body:    Option[ConvertResult]
):
  def addHeader(header: Header): ServerResponse =
    copy(headers = headers :+ header)
  def addHeaders(headerList: Seq[Header]): ServerResponse =
    copy(headers = headers ++ headerList)

  def toHttp4sResponse[F[_]](): Http4sResponse[F] =
    Http4sResponse[F](
      status  = status.toHttp4sStatus(),
      headers = Http4sHeaders(headers.map(_.toHttp4sHeader()), headers.flatMap(_.uri).map(Location(_))),
      body    = body.map(_.toStream()).getOrElse(Stream.empty)
    )

object ServerResponse:

  final class Result(status: Response.Status):
    def apply[C <: ConvertResult](content: C): ServerResponse =
      ServerResponse(status, Seq.empty, Some(content))

    def apply(content: String): ServerResponse =
      ServerResponse(status, Seq(Header.HeaderType.TextPlain), Some(ConvertResult.PlainText(content)))

  final class Redirect(status: Response.Status):
    def apply(url: String): ServerResponse =
      Uri.fromString(url) match
        case Right(uri) => ServerResponse(status, Seq(Header("location", uri.renderString, Some(uri))), None)
        case Left(ex)   => throw new Exception(ex.message)

    def apply(url: String, queryParams: Map[String, Seq[String]] = Map.empty): ServerResponse =
      Uri.fromString(bindUrlAndQueryParams(url, queryParams)) match
        case Right(uri) => ServerResponse(status, Seq(Header("location", uri.renderString, Some(uri))), None)
        case Left(ex)   => throw new Exception(ex.message)

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

  lazy val Result   = (status: Response.Status) => new Result(status)
  lazy val Redirect = (status: Response.Status) => new Redirect(status)

  /** Generates a ‘200 OK’ result. */
  val Ok = Result(Response.Status.Ok)

  /** Generates a ‘201 CREATED’ result. */
  val Created = Result(Response.Status.Created)

  /** Generates a ‘202 ACCEPTED’ result. */
  val Accepted = Result(Response.Status.Accepted)

  /** Generates a ‘203 NON_AUTHORITATIVE_INFORMATION’ result. */
  val NonAuthoritativeInformation = Result(Response.Status.NonAuthoritativeInformation)

  /** Generates a ‘204 NO_CONTENT’ result. */
  val NoContent = ServerResponse(Response.Status.NoContent, Seq.empty, None)

  /** Generates a ‘205 RESET_CONTENT’ result. */
  val ResetContent = ServerResponse(Response.Status.ResetContent, Seq.empty, None)

  /** Generates a ‘206 PARTIAL_CONTENT’ result. */
  val PartialContent = Result(Response.Status.PartialContent)

  /** Generates a ‘207 MULTI_STATUS’ result. */
  val MultiStatus = Result(Response.Status.MultiStatus)

  /** Generates a ‘300 MULTIPLE_CHOICES’ simple result. */
  val MultipleChoices: Redirect = Redirect(Response.Status.MultipleChoices)

  /** Generates a ‘301 MOVED_PERMANENTLY’ simple result. */
  val MovedPermanently: Redirect = Redirect(Response.Status.MovedPermanently)

  /** Generates a ‘302 FOUND’ simple result. */
  val Found: Redirect = Redirect(Response.Status.Found)

  /** Generates a ‘303 SEE_OTHER’ simple result. */
  val SeeOther: Redirect = Redirect(Response.Status.SeeOther)

  /** Generates a ‘304 NOT_MODIFIED’ result. */
  val NotModified: Redirect = Redirect(Response.Status.NotModified)

  /** Generates a ‘307 TEMPORARY_REDIRECT’ simple result. */
  val TemporaryRedirect: Redirect = Redirect(Response.Status.TemporaryRedirect)

  /** Generates a ‘308 PERMANENT_REDIRECT’ simple result. */
  val PermanentRedirect: Redirect = Redirect(Response.Status.PermanentRedirect)

  /** Generates a ‘400 BAD_REQUEST’ result. */
  val BadRequest = Result(Response.Status.BadRequest)

  /** Generates a ‘401 UNAUTHORIZED’ result. */
  val Unauthorized = Result(Response.Status.Unauthorized)

  /** Generates a ‘402 PAYMENT_REQUIRED’ result. */
  val PaymentRequired = Result(Response.Status.PaymentRequired)

  /** Generates a ‘403 FORBIDDEN’ result. */
  val Forbidden = Result(Response.Status.Forbidden)

  /** Generates a ‘404 NOT_FOUND’ result. */
  val NotFound = Result(Response.Status.NotFound)

  /** Generates a ‘405 METHOD_NOT_ALLOWED’ result. */
  val MethodNotAllowed = Result(Response.Status.MethodNotAllowed)

  /** Generates a ‘406 NOT_ACCEPTABLE’ result. */
  val NotAcceptable = Result(Response.Status.NotAcceptable)

  /** Generates a ‘408 REQUEST_TIMEOUT’ result. */
  val RequestTimeout = Result(Response.Status.RequestTimeout)

  /** Generates a ‘409 CONFLICT’ result. */
  val Conflict = Result(Response.Status.Conflict)

  /** Generates a ‘410 GONE’ result. */
  val Gone = Result(Response.Status.Gone)

  /** Generates a ‘412 PRECONDITION_FAILED’ result. */
  val PreconditionFailed = Result(Response.Status.PreconditionFailed)

  /** Generates a ‘413 REQUEST_ENTITY_TOO_LARGE’ result. */
  val EntityTooLarge = Result(Response.Status.RequestEntityTooLarge)

  /** Generates a ‘414 REQUEST_URI_TOO_LONG’ result. */
  val UriTooLong = Result(Response.Status.RequestUriTooLong)

  /** Generates a ‘415 UNSUPPORTED_MEDIA_TYPE’ result. */
  val UnsupportedMediaType = Result(Response.Status.UnsupportedMediaType)

  /** Generates a ‘417 EXPECTATION_FAILED’ result. */
  val ExpectationFailed = Result(Response.Status.ExpectationFailed)

  /** Generates a ‘418 IM_A_TEAPOT’ result. */
  val ImATeapot = Result(Response.Status.ImATeapot)

  /** Generates a ‘422 UNPROCESSABLE_ENTITY’ result. */
  val UnprocessableEntity = Result(Response.Status.UnprocessableEntity)

  /** Generates a ‘423 LOCKED’ result. */
  val Locked = Result(Response.Status.Locked)

  /** Generates a ‘424 FAILED_DEPENDENCY’ result. */
  val FailedDependency = Result(Response.Status.FailedDependency)

  /** Generates a ‘428 PRECONDITION_REQUIRED’ result. */
  val PreconditionRequired = Result(Response.Status.PreconditionRequired)

  /** Generates a ‘429 TOO_MANY_REQUESTS’ result. */
  val TooManyRequests = Result(Response.Status.TooManyRequests)

  /** Generates a ‘431 REQUEST_HEADER_FIELDS_TOO_LARGE’ result. */
  val RequestHeaderFieldsTooLarge = Result(Response.Status.RequestHeaderFieldsTooLarge)

  /** Generates a ‘500 INTERNAL_SERVER_ERROR’ result. */
  val InternalServerError = Result(Response.Status.InternalServerError)

  /** Generates a ‘501 NOT_IMPLEMENTED’ result. */
  val NotImplemented = Result(Response.Status.NotImplemented)

  /** Generates a ‘502 BAD_GATEWAY’ result. */
  val BadGateway = Result(Response.Status.BadGateway)

  /** Generates a ‘503 SERVICE_UNAVAILABLE’ result. */
  val ServiceUnavailable = Result(Response.Status.ServiceUnavailable)

  /** Generates a ‘504 GATEWAY_TIMEOUT’ result. */
  val GatewayTimeout = Result(Response.Status.GatewayTimeout)

  /** Generates a ‘505 HTTP_VERSION_NOT_SUPPORTED’ result. */
  val HttpVersionNotSupported = Result(Response.Status.HttpVersionNotSupported)

  /** Generates a ‘507 INSUFFICIENT_STORAGE’ result. */
  val InsufficientStorage = Result(Response.Status.InsufficientStorage)

  /** Generates a ‘511 NETWORK_AUTHENTICATION_REQUIRED’ result. */
  val NetworkAuthenticationRequired = Result(Response.Status.NetworkAuthenticationRequired)
