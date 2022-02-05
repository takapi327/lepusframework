/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.http

import org.typelevel.ci.CIString

import org.http4s.{ Header => Http4sHeader }

trait Header {

  def name:  String
  def value: String

  override def toString: String = s"$name: $value"

  def is(headerName: String): Boolean = name.equalsIgnoreCase(headerName)
}

object Header {
  case class RequestHeader(name: String, value: String) extends Header
  case class ResponseHeader(name: String, value: String) extends Header {
    def toHttp4sHeader(): Http4sHeader.Raw =
      Http4sHeader.Raw(CIString(ContentType), s"${name}/${value}")
  }

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
  val Accept                        = "Accept"
  val AcceptCharset                 = "Accept-Charset"
  val AcceptEncoding                = "Accept-Encoding"
  val AcceptLanguage                = "Accept-Language"
  val AcceptRanges                  = "Accept-Ranges"
  val AccessControlAllowCredentials = "Access-Control-Allow-Credentials"
  val AccessControlAllowHeaders     = "Access-Control-Allow-Headers"
  val AccessControlAllowMethods     = "Access-Control-Allow-Methods"
  val AccessControlAllowOrigin      = "Access-Control-Allow-Origin"
  val AccessControlExposeHeaders    = "Access-Control-Expose-Headers"
  val AccessControlMaxAge           = "Access-Control-Max-Age"
  val AccessControlRequestHeaders   = "Access-Control-Request-Headers"
  val AccessControlRequestMethod    = "Access-Control-Request-Method"
  val Age                           = "Age"
  val Allow                         = "Allow"
  val Authorization                 = "Authorization"
  val CacheControl                  = "Cache-Control"
  val Connection                    = "Connection"
  val ContentDisposition            = "Content-Disposition"
  val ContentEncoding               = "Content-Encoding"
  val ContentLanguage               = "Content-Language"
  val ContentLength                 = "Content-Length"
  val ContentLocation               = "Content-Location"
  val ContentMd5                    = "Content-MD5"
  val ContentRange                  = "Content-Range"
  val ContentTransferEncoding       = "Content-Transfer-Encoding"
  val ContentType                   = "Content-Type"
  val Cookie                        = "Cookie"
  val Date                          = "Date"
  val Etag                          = "ETag"
  val Expect                        = "Expect"
  val Expires                       = "Expires"
  val Forwarded                     = "Forwarded"
  val From                          = "From"
  val Host                          = "Host"
  val IfMatch                       = "If-Match"
  val IfModifiedSince               = "If-Modified-Since"
  val IfNoneMatch                   = "If-None-Match"
  val IfRange                       = "If-Range"
  val IfUnmodifiedSince             = "If-Unmodified-Since"
  val LastModified                  = "Last-Modified"
  val Link                          = "Link"
  val Location                      = "Location"
  val MaxForwards                   = "Max-Forwards"
  val Origin                        = "Origin"
  val Pragma                        = "Pragma"
  val ProxyAuthenticate             = "Proxy-Authenticate"
  val ProxyAuthorization            = "Proxy-Authorization"
  val Range                         = "Range"
  val Referer                       = "Referer"
  val RemoteAddress                 = "Remote-Address"
  val RetryAfter                    = "Retry-After"
  val SecWebSocketKey               = "Sec-WebSocket-Key"
  val SecWebSocketExtensions        = "Sec-WebSocket-Extensions"
  val SecWebSocketAccept            = "Sec-WebSocket-Accept"
  val SecWebSocketProtocol          = "Sec-WebSocket-Protocol"
  val SecWebSocketVersion           = "Sec-WebSocket-Version"
  val Server                        = "Server"
  val SetCookie                     = "Set-Cookie"
  val StrictTransportSecurity       = "Strict-Transport-Security"
  val Te                            = "Te"
  val Trailer                       = "Trailer"
  val TransferEncoding              = "Transfer-Encoding"
  val Upgrade                       = "Upgrade"
  val UserAgent                     = "User-Agent"
  val Vary                          = "Vary"
  val Via                           = "Via"
  val Warning                       = "Warning"
  val WwwAuthenticate               = "WWW-Authenticate"
  val XFrameOptions                 = "X-Frame-Options"
  val XForwardedFor                 = "X-Forwarded-For"
  val XForwardedHost                = "X-Forwarded-Host"
  val XForwardedPort                = "X-Forwarded-Port"
  val XForwardedProto               = "X-Forwarded-Proto"
  val XRealIp                       = "X-Real-Ip"
  val XRequestedWith                = "X-Requested-With"
  val XXSSProtection                = "X-XSS-Protection"
}
