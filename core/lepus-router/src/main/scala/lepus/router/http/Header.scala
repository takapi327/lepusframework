/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.http

import org.typelevel.ci.CIString

import org.http4s.{ Header => Http4sHeader }

import Header._
trait Header {

  def name:  String
  def value: String

  override def toString: String = s"$name: $value"
  def toSwaggerString: String = s"$name/$value"

  def is(headerName: String): Boolean = name.equalsIgnoreCase(headerName)

  def isApplication: Boolean = is("application")
  def isAudio:       Boolean = is("audio")
  def isImage:       Boolean = is("image")
  def isMessage:     Boolean = is("message")
  def isMultipart:   Boolean = is("multipart")
  def isText:        Boolean = is("text")
  def isVideo:       Boolean = is("video")
  def isFont:        Boolean = is("font")
  def isExample:     Boolean = is("example")
  def isModel:       Boolean = is("model")

  def toHttp4sHeader(): Http4sHeader.Raw =
    Http4sHeader.Raw(CIString(CONTENT_TYPE), s"${name}/${value}")
  def toHttp4sHeader(contentType: CIString): Http4sHeader.Raw =
    Http4sHeader.Raw(contentType, s"${name}/${value}")
}

object Header {
  case class RequestHeader(name: String, value: String) extends Header
  case class ResponseHeader(name: String, value: String) extends Header

  object ResponseHeader {
    val ApplicationGzip:               ResponseHeader = ResponseHeader("application", "gzip")
    val ApplicationZip:                ResponseHeader = ResponseHeader("application", "zip")
    val ApplicationJson:               ResponseHeader = ResponseHeader("application", "json")
    val ApplicationOctetStream:        ResponseHeader = ResponseHeader("application", "octet-stream")
    val ApplicationPdf:                ResponseHeader = ResponseHeader("application", "pdf")
    val ApplicationRtf:                ResponseHeader = ResponseHeader("application", "rtf")
    val ApplicationXhtml:              ResponseHeader = ResponseHeader("application", "xhtml+xml")
    val ApplicationXml:                ResponseHeader = ResponseHeader("application", "xml")
    val ApplicationXWwwFormUrlencoded: ResponseHeader = ResponseHeader("application", "x-www-form-urlencoded")

    val ImageGif:  ResponseHeader = ResponseHeader("image", "gif")
    val ImageJpeg: ResponseHeader = ResponseHeader("image", "jpeg")
    val ImagePng:  ResponseHeader = ResponseHeader("image", "png")
    val ImageTiff: ResponseHeader = ResponseHeader("image", "tiff")

    val MultipartFormData:    ResponseHeader = ResponseHeader("multipart", "form-data")
    val MultipartMixed:       ResponseHeader = ResponseHeader("multipart", "mixed")
    val MultipartAlternative: ResponseHeader = ResponseHeader("multipart", "alternative")

    val TextCacheManifest: ResponseHeader = ResponseHeader("text", "cache-manifest")
    val TextCalendar:      ResponseHeader = ResponseHeader("text", "calendar")
    val TextCss:           ResponseHeader = ResponseHeader("text", "css")
    val TextCsv:           ResponseHeader = ResponseHeader("text", "csv")
    val TextEventStream:   ResponseHeader = ResponseHeader("text", "event-stream")
    val TextJavascript:    ResponseHeader = ResponseHeader("text", "javascript")
    val TextHtml:          ResponseHeader = ResponseHeader("text", "html")
    val TextPlain:         ResponseHeader = ResponseHeader("text", "plain")
  }

  /**
   * The values listed in the following sites are defined as variables.
   * see https://www.iana.org/assignments/message-headers/message-headers.xml#perm-headers
   */
  val ACCEPT                           = "Accept"
  val ACCEPT_CHARSET                   = "Accept-Charset"
  val ACCEPT_ENCODING                  = "Accept-Encoding"
  val ACCEPT_LANGUAGE                  = "Accept-Language"
  val ACCEPT_RANGES                    = "Accept-Ranges"
  val ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials"
  val ACCESS_CONTROL_ALLOW_HEADERS     = "Access-Control-Allow-Headers"
  val ACCESS_CONTROL_ALLOW_METHODS     = "Access-Control-Allow-Methods"
  val ACCESS_CONTROL_ALLOW_ORIGIN      = "Access-Control-Allow-Origin"
  val ACCESS_CONTROL_EXPOSE_HEADERS    = "Access-Control-Expose-Headers"
  val ACCESS_CONTROL_MAX_AGE           = "Access-Control-Max-Age"
  val ACCESS_CONTROL_REQUEST_HEADERS   = "Access-Control-Request-Headers"
  val ACCESS_CONTROL_REQUEST_METHOD    = "Access-Control-Request-Method"
  val AGE                              = "Age"
  val ALLOW                            = "Allow"
  val AUTHORIZATION                    = "Authorization"
  val CACHE_CONTROL                    = "Cache-Control"
  val CONNECTION                       = "Connection"
  val CONTENT_DISPOSITION              = "Content-Disposition"
  val CONTENT_ENCODING                 = "Content-Encoding"
  val CONTENT_LANGUAGE                 = "Content-Language"
  val CONTENT_LENGTH                   = "Content-Length"
  val CONTENT_LOCATION                 = "Content-Location"
  val CONTENT_MD5                      = "Content-MD5"
  val CONTENT_RANGE                    = "Content-Range"
  val CONTENT_TRANSFER_ENCODING        = "Content-Transfer-Encoding"
  val CONTENT_TYPE                     = "Content-Type"
  val COOKIE                           = "Cookie"
  val DATE                             = "Date"
  val ETAG                             = "ETag"
  val EXPECT                           = "Expect"
  val EXPIRES                          = "Expires"
  val FORWARDED                        = "Forwarded"
  val FROM                             = "From"
  val HOST                             = "Host"
  val IF_MATCH                         = "If-Match"
  val IF_MODIFIED_SINCE                = "If-Modified-Since"
  val IF_NONE_MATCH                    = "If-None-Match"
  val IF_RANGE                         = "If-Range"
  val IF_UNMODIFIED_SINCE              = "If-Unmodified-Since"
  val LAST_MODIFIED                    = "Last-Modified"
  val LINK                             = "Link"
  val LOCATION                         = "Location"
  val MAX_FORWARDS                     = "Max-Forwards"
  val ORIGIN                           = "Origin"
  val PRAGMA                           = "Pragma"
  val PROXY_AUTHENTICATE               = "Proxy-Authenticate"
  val PROXY_AUTHORIZATION              = "Proxy-Authorization"
  val RANGE                            = "Range"
  val REFERER                          = "Referer"
  val REMOTE_ADDRESS                   = "Remote-Address"
  val RETRY_AFTER                      = "Retry-After"
  val SEC_WEBSOCKET_KEY                = "Sec-WebSocket-Key"
  val SEC_WEBSOCKET_EXTENSIONS         = "Sec-WebSocket-Extensions"
  val SEC_WEBSOCKET_ACCEPT             = "Sec-WebSocket-Accept"
  val SEC_WEBSOCKET_PROTOCOL           = "Sec-WebSocket-Protocol"
  val SEC_WEBSOCKET_VERSION            = "Sec-WebSocket-Version"
  val SERVER                           = "Server"
  val SET_COOKIE                       = "Set-Cookie"
  val STRICT_TRANSPORT_SECURITY        = "Strict-Transport-Security"
  val TE                               = "Te"
  val TRAILER                          = "Trailer"
  val TRANSFER_ENCODING                = "Transfer-Encoding"
  val UPGRADE                          = "Upgrade"
  val USERAGENT                        = "User-Agent"
  val VARY                             = "Vary"
  val VIA                              = "Via"
  val WARNING                          = "Warning"
  val WWW_AUTHENTICATE                 = "WWW-Authenticate"
  val X_FRAME_OPTIONS                  = "X-Frame-Options"
  val X_FORWARDED_FOR                  = "X-Forwarded-For"
  val X_FORWARDED_HOST                 = "X-Forwarded-Host"
  val X_FORWARDED_PORT                 = "X-Forwarded-Port"
  val X_FORWARDED_PROTO                = "X-Forwarded-Proto"
  val X_REAL_IP                        = "X-Real-Ip"
  val X_REQUESTED_WITH                 = "X-Requested-With"
  val X_XSS_PROTECTION                 = "X-XSS-Protection"
}
