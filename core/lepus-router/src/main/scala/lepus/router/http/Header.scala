/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router.http

import org.typelevel.ci.CIString

import org.http4s.{ Uri, Header => Http4sHeader }

import Header.*
trait Header(name: String, val value: String, val uri: Option[Uri] = None):

  override def toString: String = s"$name: $value"
  def toString(split: String = "/"): String = s"$name$split$value"

  def is(headerName: String): Boolean = name.equalsIgnoreCase(headerName)

  val isApplication: Boolean = is("application")
  val isAudio:       Boolean = is("audio")
  val isImage:       Boolean = is("image")
  val isMessage:     Boolean = is("message")
  val isMultipart:   Boolean = is("multipart")
  val isText:        Boolean = is("text")
  val isVideo:       Boolean = is("video")
  val isFont:        Boolean = is("font")
  val isExample:     Boolean = is("example")
  val isModel:       Boolean = is("model")

  def toHttp4sHeader(): Http4sHeader.Raw =
    Http4sHeader.Raw(CIString(CONTENT_TYPE), this.toString())
  def toHttp4sHeader(contentType: CIString): Http4sHeader.Raw =
    Http4sHeader.Raw(contentType, this.toString())

object Header:

  def apply(name: String, value: String, uri: Option[Uri] = None): Header =
    new Header(name, value, uri) {}

  enum HeaderType:
    case ApplicationGzip               extends HeaderType, Header("application", "gzip")
    case ApplicationZip                extends HeaderType, Header("application", "zip")
    case ApplicationJson               extends HeaderType, Header("application", "json")
    case ApplicationOctetStream        extends HeaderType, Header("application", "octet-stream")
    case ApplicationPdf                extends HeaderType, Header("application", "pdf")
    case ApplicationRtf                extends HeaderType, Header("application", "rtf")
    case ApplicationXhtml              extends HeaderType, Header("application", "xhtml+xml")
    case ApplicationXml                extends HeaderType, Header("application", "xml")
    case ApplicationXWwwFormUrlencoded extends HeaderType, Header("application", "x-www-form-urlencoded")
    case ImageGif                      extends HeaderType, Header("image", "gif")
    case ImageJpeg                     extends HeaderType, Header("image", "jpeg")
    case ImagePng                      extends HeaderType, Header("image", "png")
    case ImageTiff                     extends HeaderType, Header("image", "tiff")
    case MultipartFormData             extends HeaderType, Header("multipart", "form-data")
    case MultipartMixed                extends HeaderType, Header("multipart", "mixed")
    case MultipartAlternative          extends HeaderType, Header("multipart", "alternative")
    case TextCacheManifest             extends HeaderType, Header("text", "cache-manifest")
    case TextCalendar                  extends HeaderType, Header("text", "calendar")
    case TextCss                       extends HeaderType, Header("text", "css")
    case TextCsv                       extends HeaderType, Header("text", "csv")
    case TextEventStream               extends HeaderType, Header("text", "event-stream")
    case TextJavascript                extends HeaderType, Header("text", "javascript")
    case TextHtml                      extends HeaderType, Header("text", "html")
    case TextPlain                     extends HeaderType, Header("text", "plain")

  /** The values listed in the following sites are defined as variables. see
   * https://www.iana.org/assignments/message-headers/message-headers.xml#perm-headers
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
