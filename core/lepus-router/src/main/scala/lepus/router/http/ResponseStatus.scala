/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.http

import org.http4s.{ Status => Http4sStatus }

class ResponseStatus(val code: Int) {
  def isInformational(): Boolean = code / 100 == 1
  def isSuccess():       Boolean = code / 100 == 2
  def isRedirect():      Boolean = code / 100 == 3
  def isClientError():   Boolean = code / 100 == 4
  def isServerError():   Boolean = code / 100 == 5

  def toHttp4sStatus(): Http4sStatus =
    Http4sStatus.fromInt(code).getOrElse(throw new IllegalArgumentException(s"Invalid status code: $code"))
}

object ResponseStatus extends StatusCodes {

  def apply(code: Int): ResponseStatus = new ResponseStatus(code)

  val Continue:           ResponseStatus = ResponseStatus(CONTINUE)
  val SwitchingProtocols: ResponseStatus = ResponseStatus(SWITCHING_PROTOCOLS)

  val Ok:                          ResponseStatus = ResponseStatus(OK)
  val Created:                     ResponseStatus = ResponseStatus(CREATED)
  val Accepted:                    ResponseStatus = ResponseStatus(ACCEPTED)
  val NonAuthoritativeInformation: ResponseStatus = ResponseStatus(NON_AUTHORITATIVE_INFORMATION)
  val NoContent:                   ResponseStatus = ResponseStatus(NO_CONTENT)
  val ResetContent:                ResponseStatus = ResponseStatus(RESET_CONTENT)
  val PartialContent:              ResponseStatus = ResponseStatus(PARTIAL_CONTENT)
  val MultiStatus:                 ResponseStatus = ResponseStatus(MULTI_STATUS)

  val MultipleChoices:   ResponseStatus = ResponseStatus(MULTIPLE_CHOICES)
  val MovedPermanently:  ResponseStatus = ResponseStatus(MOVED_PERMANENTLY)
  val Found:             ResponseStatus = ResponseStatus(FOUND)
  val SeeOther:          ResponseStatus = ResponseStatus(SEE_OTHER)
  val NotModified:       ResponseStatus = ResponseStatus(NOT_MODIFIED)
  val UseProxy:          ResponseStatus = ResponseStatus(USE_PROXY)
  val TemporaryRedirect: ResponseStatus = ResponseStatus(TEMPORARY_REDIRECT)
  val PermanentRedirect: ResponseStatus = ResponseStatus(PERMANENT_REDIRECT)

  val BadRequest:                   ResponseStatus = ResponseStatus(BAD_REQUEST)
  val Unauthorized:                 ResponseStatus = ResponseStatus(UNAUTHORIZED)
  val PaymentRequired:              ResponseStatus = ResponseStatus(PAYMENT_REQUIRED)
  val Forbidden:                    ResponseStatus = ResponseStatus(FORBIDDEN)
  val NotFound:                     ResponseStatus = ResponseStatus(NOT_FOUND)
  val MethodNotAllowed:             ResponseStatus = ResponseStatus(METHOD_NOT_ALLOWED)
  val NotAcceptable:                ResponseStatus = ResponseStatus(NOT_ACCEPTABLE)
  val ProxyAuthenticationRequired:  ResponseStatus = ResponseStatus(PROXY_AUTHENTICATION_REQUIRED)
  val RequestTimeout:               ResponseStatus = ResponseStatus(REQUEST_TIMEOUT)
  val Conflict:                     ResponseStatus = ResponseStatus(CONFLICT)
  val Gone:                         ResponseStatus = ResponseStatus(GONE)
  val LengthRequired:               ResponseStatus = ResponseStatus(LENGTH_REQUIRED)
  val PreconditionFailed:           ResponseStatus = ResponseStatus(PRECONDITION_FAILED)
  val RequestEntityTooLarge:        ResponseStatus = ResponseStatus(REQUEST_ENTITY_TOO_LARGE)
  val RequestUriTooLong:            ResponseStatus = ResponseStatus(REQUEST_URI_TOO_LONG)
  val UnsupportedMediaType:         ResponseStatus = ResponseStatus(UNSUPPORTED_MEDIA_TYPE)
  val RequestedRangeNotSatisfiable: ResponseStatus = ResponseStatus(REQUESTED_RANGE_NOT_SATISFIABLE)
  val ExpectationFailed:            ResponseStatus = ResponseStatus(EXPECTATION_FAILED)
  val ImATeapot:                    ResponseStatus = ResponseStatus(IM_A_TEAPOT)
  val UnprocessableEntity:          ResponseStatus = ResponseStatus(UNPROCESSABLE_ENTITY)
  val Locked:                       ResponseStatus = ResponseStatus(LOCKED)
  val FailedDependency:             ResponseStatus = ResponseStatus(FAILED_DEPENDENCY)
  val UpgradeRequired:              ResponseStatus = ResponseStatus(UPGRADE_REQUIRED)
  val PreconditionRequired:         ResponseStatus = ResponseStatus(PRECONDITION_REQUIRED)
  val TooManyRequests:              ResponseStatus = ResponseStatus(TOO_MANY_REQUESTS)
  val RequestHeaderFieldsTooLarge:  ResponseStatus = ResponseStatus(REQUEST_HEADER_FIELDS_TOO_LARGE)

  val InternalServerError:           ResponseStatus = ResponseStatus(INTERNAL_SERVER_ERROR)
  val NotImplemented:                ResponseStatus = ResponseStatus(NOT_IMPLEMENTED)
  val BadGateway:                    ResponseStatus = ResponseStatus(BAD_GATEWAY)
  val ServiceUnavailable:            ResponseStatus = ResponseStatus(SERVICE_UNAVAILABLE)
  val GatewayTimeout:                ResponseStatus = ResponseStatus(GATEWAY_TIMEOUT)
  val HttpVersionNotSupported:       ResponseStatus = ResponseStatus(HTTP_VERSION_NOT_SUPPORTED)
  val InsufficientStorage:           ResponseStatus = ResponseStatus(INSUFFICIENT_STORAGE)
  val NetworkAuthenticationRequired: ResponseStatus = ResponseStatus(NETWORK_AUTHENTICATION_REQUIRED)
}

trait StatusCodes {
  val CONTINUE            = 100
  val SWITCHING_PROTOCOLS = 101

  val OK                            = 200
  val CREATED                       = 201
  val ACCEPTED                      = 202
  val NON_AUTHORITATIVE_INFORMATION = 203
  val NO_CONTENT                    = 204
  val RESET_CONTENT                 = 205
  val PARTIAL_CONTENT               = 206
  val MULTI_STATUS                  = 207

  val MULTIPLE_CHOICES   = 300
  val MOVED_PERMANENTLY  = 301
  val FOUND              = 302
  val SEE_OTHER          = 303
  val NOT_MODIFIED       = 304
  val USE_PROXY          = 305
  val TEMPORARY_REDIRECT = 307
  val PERMANENT_REDIRECT = 308

  val BAD_REQUEST                     = 400
  val UNAUTHORIZED                    = 401
  val PAYMENT_REQUIRED                = 402
  val FORBIDDEN                       = 403
  val NOT_FOUND                       = 404
  val METHOD_NOT_ALLOWED              = 405
  val NOT_ACCEPTABLE                  = 406
  val PROXY_AUTHENTICATION_REQUIRED   = 407
  val REQUEST_TIMEOUT                 = 408
  val CONFLICT                        = 409
  val GONE                            = 410
  val LENGTH_REQUIRED                 = 411
  val PRECONDITION_FAILED             = 412
  val REQUEST_ENTITY_TOO_LARGE        = 413
  val REQUEST_URI_TOO_LONG            = 414
  val UNSUPPORTED_MEDIA_TYPE          = 415
  val REQUESTED_RANGE_NOT_SATISFIABLE = 416
  val EXPECTATION_FAILED              = 417
  val IM_A_TEAPOT                     = 418
  val UNPROCESSABLE_ENTITY            = 422
  val LOCKED                          = 423
  val FAILED_DEPENDENCY               = 424
  val UPGRADE_REQUIRED                = 426
  val PRECONDITION_REQUIRED           = 428
  val TOO_MANY_REQUESTS               = 429
  val REQUEST_HEADER_FIELDS_TOO_LARGE = 431

  val INTERNAL_SERVER_ERROR           = 500
  val NOT_IMPLEMENTED                 = 501
  val BAD_GATEWAY                     = 502
  val SERVICE_UNAVAILABLE             = 503
  val GATEWAY_TIMEOUT                 = 504
  val HTTP_VERSION_NOT_SUPPORTED      = 505
  val INSUFFICIENT_STORAGE            = 507
  val NETWORK_AUTHENTICATION_REQUIRED = 511
}
