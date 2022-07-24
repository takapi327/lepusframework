/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import io.circe.Encoder

import org.http4s.{ Status => Http4sStatus }

import lepus.router.model.Schema

/** API Response Value
  *
  * @param status
  *   Response Status Code
  * @param headers
  *   List of headers given to the response
  * @param description
  *   Response Description
  */
case class Response[T: Encoder](
  status:           Response.Status,
  headers:          List[Response.CustomHeader[?]] = List.empty,
  description:      String
)(using val schema: Schema[T])

object Response:

  def build[T: Encoder: Schema](
    status:      Status,
    headers:     List[CustomHeader[?]],
    description: String
  ): Response[T] =
    Response[T](
      status      = status,
      headers     = headers,
      description = description
    )

  case class CustomHeader[T](
    name:             String,
    description:      String
  )(using val schema: Schema[T])

  case class Status(enumStatus: StatusCode):
    private val INFORMATIONAL_CODE = 1
    private val SUCCESS_CODE       = 2
    private val REDIRECT_CODE      = 3
    private val CLIENT_ERROR_CODE  = 4
    private val SERVER_ERROR_CODE  = 5

    val isInformational: Boolean = enumStatus.toCode / 100 == INFORMATIONAL_CODE
    val isSuccess:       Boolean = enumStatus.toCode / 100 == SUCCESS_CODE
    val isRedirect:      Boolean = enumStatus.toCode / 100 == REDIRECT_CODE
    val isClientError:   Boolean = enumStatus.toCode / 100 == CLIENT_ERROR_CODE
    val isServerError:   Boolean = enumStatus.toCode / 100 == SERVER_ERROR_CODE

    def toHttp4sStatus(): Http4sStatus =
      Http4sStatus
        .fromInt(enumStatus.toCode)
        .getOrElse(throw new IllegalArgumentException(s"Invalid status code: ${ enumStatus.toCode }"))
  end Status

  object Status:
    val Continue:           Status = Status(StatusCode.CONTINUE)
    val SwitchingProtocols: Status = Status(StatusCode.SWITCHING_PROTOCOLS)

    val Ok:                          Status = Status(StatusCode.OK)
    val Created:                     Status = Status(StatusCode.CREATED)
    val Accepted:                    Status = Status(StatusCode.ACCEPTED)
    val NonAuthoritativeInformation: Status = Status(StatusCode.NON_AUTHORITATIVE_INFORMATION)
    val NoContent:                   Status = Status(StatusCode.NO_CONTENT)
    val ResetContent:                Status = Status(StatusCode.RESET_CONTENT)
    val PartialContent:              Status = Status(StatusCode.PARTIAL_CONTENT)
    val MultiStatus:                 Status = Status(StatusCode.MULTI_STATUS)

    val MultipleChoices:   Status = Status(StatusCode.MULTIPLE_CHOICES)
    val MovedPermanently:  Status = Status(StatusCode.MOVED_PERMANENTLY)
    val Found:             Status = Status(StatusCode.FOUND)
    val SeeOther:          Status = Status(StatusCode.SEE_OTHER)
    val NotModified:       Status = Status(StatusCode.NOT_MODIFIED)
    val UseProxy:          Status = Status(StatusCode.USE_PROXY)
    val TemporaryRedirect: Status = Status(StatusCode.TEMPORARY_REDIRECT)
    val PermanentRedirect: Status = Status(StatusCode.PERMANENT_REDIRECT)

    val BadRequest:                   Status = Status(StatusCode.BAD_REQUEST)
    val Unauthorized:                 Status = Status(StatusCode.UNAUTHORIZED)
    val PaymentRequired:              Status = Status(StatusCode.PAYMENT_REQUIRED)
    val Forbidden:                    Status = Status(StatusCode.FORBIDDEN)
    val NotFound:                     Status = Status(StatusCode.NOT_FOUND)
    val MethodNotAllowed:             Status = Status(StatusCode.METHOD_NOT_ALLOWED)
    val NotAcceptable:                Status = Status(StatusCode.NOT_ACCEPTABLE)
    val ProxyAuthenticationRequired:  Status = Status(StatusCode.PROXY_AUTHENTICATION_REQUIRED)
    val RequestTimeout:               Status = Status(StatusCode.REQUEST_TIMEOUT)
    val Conflict:                     Status = Status(StatusCode.CONFLICT)
    val Gone:                         Status = Status(StatusCode.GONE)
    val LengthRequired:               Status = Status(StatusCode.LENGTH_REQUIRED)
    val PreconditionFailed:           Status = Status(StatusCode.PRECONDITION_FAILED)
    val RequestEntityTooLarge:        Status = Status(StatusCode.REQUEST_ENTITY_TOO_LARGE)
    val RequestUriTooLong:            Status = Status(StatusCode.REQUEST_URI_TOO_LONG)
    val UnsupportedMediaType:         Status = Status(StatusCode.UNSUPPORTED_MEDIA_TYPE)
    val RequestedRangeNotSatisfiable: Status = Status(StatusCode.REQUESTED_RANGE_NOT_SATISFIABLE)
    val ExpectationFailed:            Status = Status(StatusCode.EXPECTATION_FAILED)
    val ImATeapot:                    Status = Status(StatusCode.IM_A_TEAPOT)
    val UnprocessableEntity:          Status = Status(StatusCode.UNPROCESSABLE_ENTITY)
    val Locked:                       Status = Status(StatusCode.LOCKED)
    val FailedDependency:             Status = Status(StatusCode.FAILED_DEPENDENCY)
    val UpgradeRequired:              Status = Status(StatusCode.UPGRADE_REQUIRED)
    val PreconditionRequired:         Status = Status(StatusCode.PRECONDITION_REQUIRED)
    val TooManyRequests:              Status = Status(StatusCode.TOO_MANY_REQUESTS)
    val RequestHeaderFieldsTooLarge:  Status = Status(StatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE)

    val InternalServerError:           Status = Status(StatusCode.INTERNAL_SERVER_ERROR)
    val NotImplemented:                Status = Status(StatusCode.NOT_IMPLEMENTED)
    val BadGateway:                    Status = Status(StatusCode.BAD_GATEWAY)
    val ServiceUnavailable:            Status = Status(StatusCode.SERVICE_UNAVAILABLE)
    val GatewayTimeout:                Status = Status(StatusCode.GATEWAY_TIMEOUT)
    val HttpVersionNotSupported:       Status = Status(StatusCode.HTTP_VERSION_NOT_SUPPORTED)
    val InsufficientStorage:           Status = Status(StatusCode.INSUFFICIENT_STORAGE)
    val NetworkAuthenticationRequired: Status = Status(StatusCode.NETWORK_AUTHENTICATION_REQUIRED)
end Response

enum StatusCode(code: Int):
  override def toString: String = code.toString
  def toCode:            Int    = code

  case CONTINUE                        extends StatusCode(100)
  case SWITCHING_PROTOCOLS             extends StatusCode(101)
  case OK                              extends StatusCode(200)
  case CREATED                         extends StatusCode(201)
  case ACCEPTED                        extends StatusCode(202)
  case NON_AUTHORITATIVE_INFORMATION   extends StatusCode(203)
  case NO_CONTENT                      extends StatusCode(204)
  case RESET_CONTENT                   extends StatusCode(205)
  case PARTIAL_CONTENT                 extends StatusCode(206)
  case MULTI_STATUS                    extends StatusCode(207)
  case MULTIPLE_CHOICES                extends StatusCode(300)
  case MOVED_PERMANENTLY               extends StatusCode(301)
  case FOUND                           extends StatusCode(302)
  case SEE_OTHER                       extends StatusCode(303)
  case NOT_MODIFIED                    extends StatusCode(304)
  case USE_PROXY                       extends StatusCode(305)
  case TEMPORARY_REDIRECT              extends StatusCode(307)
  case PERMANENT_REDIRECT              extends StatusCode(308)
  case BAD_REQUEST                     extends StatusCode(400)
  case UNAUTHORIZED                    extends StatusCode(401)
  case PAYMENT_REQUIRED                extends StatusCode(402)
  case FORBIDDEN                       extends StatusCode(403)
  case NOT_FOUND                       extends StatusCode(404)
  case METHOD_NOT_ALLOWED              extends StatusCode(405)
  case NOT_ACCEPTABLE                  extends StatusCode(406)
  case PROXY_AUTHENTICATION_REQUIRED   extends StatusCode(407)
  case REQUEST_TIMEOUT                 extends StatusCode(408)
  case CONFLICT                        extends StatusCode(409)
  case GONE                            extends StatusCode(410)
  case LENGTH_REQUIRED                 extends StatusCode(411)
  case PRECONDITION_FAILED             extends StatusCode(412)
  case REQUEST_ENTITY_TOO_LARGE        extends StatusCode(413)
  case REQUEST_URI_TOO_LONG            extends StatusCode(414)
  case UNSUPPORTED_MEDIA_TYPE          extends StatusCode(415)
  case REQUESTED_RANGE_NOT_SATISFIABLE extends StatusCode(416)
  case EXPECTATION_FAILED              extends StatusCode(417)
  case IM_A_TEAPOT                     extends StatusCode(418)
  case UNPROCESSABLE_ENTITY            extends StatusCode(422)
  case LOCKED                          extends StatusCode(423)
  case FAILED_DEPENDENCY               extends StatusCode(424)
  case UPGRADE_REQUIRED                extends StatusCode(426)
  case PRECONDITION_REQUIRED           extends StatusCode(428)
  case TOO_MANY_REQUESTS               extends StatusCode(429)
  case REQUEST_HEADER_FIELDS_TOO_LARGE extends StatusCode(431)
  case INTERNAL_SERVER_ERROR           extends StatusCode(500)
  case NOT_IMPLEMENTED                 extends StatusCode(501)
  case BAD_GATEWAY                     extends StatusCode(502)
  case SERVICE_UNAVAILABLE             extends StatusCode(503)
  case GATEWAY_TIMEOUT                 extends StatusCode(504)
  case HTTP_VERSION_NOT_SUPPORTED      extends StatusCode(505)
  case INSUFFICIENT_STORAGE            extends StatusCode(507)
  case NETWORK_AUTHENTICATION_REQUIRED extends StatusCode(511)
