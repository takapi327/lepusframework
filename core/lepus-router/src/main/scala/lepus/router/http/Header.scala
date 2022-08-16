/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import org.typelevel.ci.CIString

import org.http4s.{ Uri, Header as Http4sHeader }

import lepus.router.model.Schema

import Header.*
trait Header(
  name:      String,
  value:     String,
  uri:       Option[Uri] = None,
  fieldName: FieldName   = FieldName.ContentType
):

  def getValue: String      = value
  def getUri:   Option[Uri] = uri

  override def toString:             String = s"$name: $value"
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
    Http4sHeader.Raw(fieldName.toCIString, this.toString())
  def toHttp4sHeader(contentType: CIString): Http4sHeader.Raw =
    Http4sHeader.Raw(contentType, this.toString())
  def toHttp4sHeader(fieldName: FieldName): Http4sHeader.Raw =
    Http4sHeader.Raw(fieldName.toCIString, this.toString())

object Header:

  def apply(
    name:      String,
    value:     String,
    uri:       Option[Uri] = None,
    fieldName: FieldName = FieldName.ContentType
  ): Header =
    new Header(name, value, uri, fieldName) {}

  case class CustomHeader[T: Schema](
    name:        String,
    value:       T,
    uri:         Option[Uri] = None,
    fieldName:   FieldName   = FieldName.ContentType,
    description: String      = ""
  ) extends Header(name, value.toString, uri, fieldName):
    val schema: Schema[T] = summon[Schema[T]]

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
  enum FieldName(val name: String):
    def toCIString: CIString = CIString(name)
    case Accept                        extends FieldName("Accept")
    case AcceptCharset                 extends FieldName("Accept-Charset")
    case AcceptEncoding                extends FieldName("Accept-Encoding")
    case AcceptLanguage                extends FieldName("Accept-Language")
    case AcceptRanges                  extends FieldName("Accept-Ranges")
    case AccessControlAllowCredentials extends FieldName("Access-Control-Allow-Credentials")
    case AccessControlAllowHeaders     extends FieldName("Access-Control-Allow-Headers")
    case AccessControlAllowMethods     extends FieldName("Access-Control-Allow-Methods")
    case AccessControlAllowOrigin      extends FieldName("Access-Control-Allow-Origin")
    case AccessControlExposeHeaders    extends FieldName("Access-Control-Expose-Headers")
    case AccessControlMaxAge           extends FieldName("Access-Control-Max-Age")
    case AccessControlRequestHeaders   extends FieldName("Access-Control-Request-Headers")
    case AccessControlRequestMethod    extends FieldName("Access-Control-Request-Method")
    case Age                           extends FieldName("Age")
    case Allow                         extends FieldName("Allow")
    case Authorization                 extends FieldName("Authorization")
    case CacheControl                  extends FieldName("Cache-Control")
    case Connection                    extends FieldName("Connection")
    case ContentDisposition            extends FieldName("Content-Disposition")
    case ContentEncoding               extends FieldName("Content-Encoding")
    case ContentLanguage               extends FieldName("Content-Language")
    case ContentLength                 extends FieldName("Content-Length")
    case ContentLocation               extends FieldName("Content-Location")
    case ContentMd5                    extends FieldName("Content-MD5")
    case ContentRange                  extends FieldName("Content-Range")
    case ContentTransferEncoding       extends FieldName("Content-Transfer-Encoding")
    case ContentType                   extends FieldName("Content-Type")
    case Cookie                        extends FieldName("Cookie")
    case Date                          extends FieldName("Date")
    case Etag                          extends FieldName("ETag")
    case Expect                        extends FieldName("Expect")
    case Expires                       extends FieldName("Expires")
    case Forwarded                     extends FieldName("Forwarded")
    case From                          extends FieldName("From")
    case Host                          extends FieldName("Host")
    case IfMatch                       extends FieldName("If-Match")
    case IfModified_since              extends FieldName("If-Modified-Since")
    case IfNoneMatch                   extends FieldName("If-None-Match")
    case IfRange                       extends FieldName("If-Range")
    case IfUnmodifiedSince             extends FieldName("If-Unmodified-Since")
    case LastModified                  extends FieldName("Last-Modified")
    case Link                          extends FieldName("Link")
    case Location                      extends FieldName("Location")
    case MaxForwards                   extends FieldName("Max-Forwards")
    case Origin                        extends FieldName("Origin")
    case Pragma                        extends FieldName("Pragma")
    case ProxyAuthenticate             extends FieldName("Proxy-Authenticate")
    case ProxyAuthorization            extends FieldName("Proxy-Authorization")
    case Range                         extends FieldName("Range")
    case Referer                       extends FieldName("Referer")
    case RemoteAddress                 extends FieldName("Remote-Address")
    case RetryAfter                    extends FieldName("Retry-After")
    case SecWebsocketKey               extends FieldName("Sec-WebSocket-Key")
    case SecWebsocketExtensions        extends FieldName("Sec-WebSocket-Extensions")
    case SecWebsocketAccept            extends FieldName("Sec-WebSocket-Accept")
    case SecWebsocketProtocol          extends FieldName("Sec-WebSocket-Protocol")
    case SecWebsocketVersion           extends FieldName("Sec-WebSocket-Version")
    case Server                        extends FieldName("Server")
    case SetCookie                     extends FieldName("Set-Cookie")
    case StrictTransportSecurity       extends FieldName("Strict-Transport-Security")
    case Te                            extends FieldName("Te")
    case Trailer                       extends FieldName("Trailer")
    case TransferEncoding              extends FieldName("Transfer-Encoding")
    case Upgrade                       extends FieldName("Upgrade")
    case Useragent                     extends FieldName("User-Agent")
    case Vary                          extends FieldName("Vary")
    case Via                           extends FieldName("Via")
    case Warning                       extends FieldName("Warning")
    case WwwAuthenticate               extends FieldName("WWW-Authenticate")
    case XFrameOptions                 extends FieldName("X-Frame-Options")
    case XForwardedFor                 extends FieldName("X-Forwarded-For")
    case XForwardedHost                extends FieldName("X-Forwarded-Host")
    case XForwardedPort                extends FieldName("X-Forwarded-Port")
    case XForwardedProto               extends FieldName("X-Forwarded-Proto")
    case XRealIp                       extends FieldName("X-Real-Ip")
    case XRequestedWith                extends FieldName("X-Requested-With")
    case XXssProtection                extends FieldName("X-XSS-Protection")
