/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import scala.annotation.targetName

import org.typelevel.ci.CIString

import org.http4s.{ Uri, Header as Http4sHeader }

import lepus.router.model.Schema

import Header.*
trait Header(
  name:  FieldName,
  value: String,
  uri:   Option[Uri] = None
):

  def getName:  FieldName   = name
  def getValue: String      = value
  def getUri:   Option[Uri] = uri

  override def toString: String = s"${ name.name }: $value"

  def is(name: FieldName): Boolean = this.name == name

  def toHttp4sHeader(): Http4sHeader.Raw =
    Http4sHeader.Raw(name.toCIString, value)
  def toHttp4sHeader(contentType: CIString): Http4sHeader.Raw =
    Http4sHeader.Raw(contentType, value)
  def toHttp4sHeader(name: FieldName): Http4sHeader.Raw =
    Http4sHeader.Raw(name.toCIString, value)

object Header:

  def apply(
    name:  FieldName,
    value: String,
    uri:   Option[Uri] = None
  ): Header =
    new Header(name, value, uri) {}

  case class CustomHeader[T: Schema](
    name:        FieldName,
    value:       String,
    uri:         Option[Uri] = None,
    description: String      = ""
  ) extends Header(name, value, uri):
    val schema: Schema[T] = summon[Schema[T]]

  enum HeaderType(value: String) extends Header(FieldName.ContentType, value):
    case ApplicationGzip               extends HeaderType("application/gzip")
    case ApplicationZip                extends HeaderType("application/zip")
    case ApplicationJson               extends HeaderType("application/json")
    case ApplicationOctetStream        extends HeaderType("application/octet-stream")
    case ApplicationPdf                extends HeaderType("application/pdf")
    case ApplicationRtf                extends HeaderType("application/rtf")
    case ApplicationXhtml              extends HeaderType("application/xhtml+xml")
    case ApplicationXml                extends HeaderType("application/xml")
    case ApplicationXWwwFormUrlencoded extends HeaderType("application/x-www-form-urlencoded")
    case ImageGif                      extends HeaderType("image/gif")
    case ImageJpeg                     extends HeaderType("image/jpeg")
    case ImagePng                      extends HeaderType("image/png")
    case ImageTiff                     extends HeaderType("image/tiff")
    case MultipartFormData             extends HeaderType("multipart/form-data")
    case MultipartMixed                extends HeaderType("multipart/mixed")
    case MultipartAlternative          extends HeaderType("multipart/alternative")
    case TextCacheManifest             extends HeaderType("text/cache-manifest")
    case TextCalendar                  extends HeaderType("text/calendar")
    case TextCss                       extends HeaderType("text/css")
    case TextCsv                       extends HeaderType("text/csv")
    case TextEventStream               extends HeaderType("text/event-stream")
    case TextJavascript                extends HeaderType("text/javascript")
    case TextHtml                      extends HeaderType("text/html")
    case TextPlain                     extends HeaderType("text/plain")

  trait FieldName(val name: String):
    def toCIString: CIString = CIString(name)

  object FieldName:
    /** The values listed in the following sites are defined as variables. see
      * https://www.iana.org/assignments/message-headers/message-headers.xml#perm-headers
      */
    case object Accept                        extends FieldName("Accept")
    case object AcceptCharset                 extends FieldName("Accept-Charset")
    case object AcceptEncoding                extends FieldName("Accept-Encoding")
    case object AcceptLanguage                extends FieldName("Accept-Language")
    case object AcceptRanges                  extends FieldName("Accept-Ranges")
    case object AccessControlAllowCredentials extends FieldName("Access-Control-Allow-Credentials")
    case object AccessControlAllowHeaders     extends FieldName("Access-Control-Allow-Headers")
    case object AccessControlAllowMethods     extends FieldName("Access-Control-Allow-Methods")
    case object AccessControlAllowOrigin      extends FieldName("Access-Control-Allow-Origin")
    case object AccessControlExposeHeaders    extends FieldName("Access-Control-Expose-Headers")
    case object AccessControlMaxAge           extends FieldName("Access-Control-Max-Age")
    case object AccessControlRequestHeaders   extends FieldName("Access-Control-Request-Headers")
    case object AccessControlRequestMethod    extends FieldName("Access-Control-Request-Method")
    case object Age                           extends FieldName("Age")
    case object Allow                         extends FieldName("Allow")
    case object Authorization                 extends FieldName("Authorization")
    case object CacheControl                  extends FieldName("Cache-Control")
    case object Connection                    extends FieldName("Connection")
    case object ContentDisposition            extends FieldName("Content-Disposition")
    case object ContentEncoding               extends FieldName("Content-Encoding")
    case object ContentLanguage               extends FieldName("Content-Language")
    case object ContentLength                 extends FieldName("Content-Length")
    case object ContentLocation               extends FieldName("Content-Location")
    case object ContentMd5                    extends FieldName("Content-MD5")
    case object ContentRange                  extends FieldName("Content-Range")
    case object ContentTransferEncoding       extends FieldName("Content-Transfer-Encoding")
    case object ContentType                   extends FieldName("Content-Type")
    case object Cookie                        extends FieldName("Cookie")
    case object Date                          extends FieldName("Date")
    case object Etag                          extends FieldName("ETag")
    case object Expect                        extends FieldName("Expect")
    case object Expires                       extends FieldName("Expires")
    case object Forwarded                     extends FieldName("Forwarded")
    case object From                          extends FieldName("From")
    case object Host                          extends FieldName("Host")
    case object IfMatch                       extends FieldName("If-Match")
    case object IfModified_since              extends FieldName("If-Modified-Since")
    case object IfNoneMatch                   extends FieldName("If-None-Match")
    case object IfRange                       extends FieldName("If-Range")
    case object IfUnmodifiedSince             extends FieldName("If-Unmodified-Since")
    case object LastModified                  extends FieldName("Last-Modified")
    case object Link                          extends FieldName("Link")
    case object Location                      extends FieldName("Location")
    case object MaxForwards                   extends FieldName("Max-Forwards")
    case object Origin                        extends FieldName("Origin")
    case object Pragma                        extends FieldName("Pragma")
    case object ProxyAuthenticate             extends FieldName("Proxy-Authenticate")
    case object ProxyAuthorization            extends FieldName("Proxy-Authorization")
    case object Range                         extends FieldName("Range")
    case object Referer                       extends FieldName("Referer")
    case object RemoteAddress                 extends FieldName("Remote-Address")
    case object RetryAfter                    extends FieldName("Retry-After")
    case object SecWebsocketKey               extends FieldName("Sec-WebSocket-Key")
    case object SecWebsocketExtensions        extends FieldName("Sec-WebSocket-Extensions")
    case object SecWebsocketAccept            extends FieldName("Sec-WebSocket-Accept")
    case object SecWebsocketProtocol          extends FieldName("Sec-WebSocket-Protocol")
    case object SecWebsocketVersion           extends FieldName("Sec-WebSocket-Version")
    case object Server                        extends FieldName("Server")
    case object SetCookie                     extends FieldName("Set-Cookie")
    case object StrictTransportSecurity       extends FieldName("Strict-Transport-Security")
    case object Te                            extends FieldName("Te")
    case object Trailer                       extends FieldName("Trailer")
    case object TransferEncoding              extends FieldName("Transfer-Encoding")
    case object Upgrade                       extends FieldName("Upgrade")
    case object Useragent                     extends FieldName("User-Agent")
    case object Vary                          extends FieldName("Vary")
    case object Via                           extends FieldName("Via")
    case object Warning                       extends FieldName("Warning")
    case object WwwAuthenticate               extends FieldName("WWW-Authenticate")
    case object XFrameOptions                 extends FieldName("X-Frame-Options")
    case object XForwardedFor                 extends FieldName("X-Forwarded-For")
    case object XForwardedHost                extends FieldName("X-Forwarded-Host")
    case object XForwardedPort                extends FieldName("X-Forwarded-Port")
    case object XForwardedProto               extends FieldName("X-Forwarded-Proto")
    case object XRealIp                       extends FieldName("X-Real-Ip")
    case object XRequestedWith                extends FieldName("X-Requested-With")
    case object XXssProtection                extends FieldName("X-XSS-Protection")

    def apply(name: String): FieldName =
      new FieldName(name) {}

    val values: Set[FieldName] =
      Set(
        Accept,
        AcceptCharset,
        AcceptEncoding,
        AcceptLanguage,
        AcceptRanges,
        AccessControlAllowCredentials,
        AccessControlAllowHeaders,
        AccessControlAllowMethods,
        AccessControlAllowOrigin,
        AccessControlExposeHeaders,
        AccessControlMaxAge,
        AccessControlRequestHeaders,
        AccessControlRequestMethod,
        Age,
        Allow,
        Authorization,
        CacheControl,
        Connection,
        ContentDisposition,
        ContentEncoding,
        ContentLanguage,
        ContentLength,
        ContentLocation,
        ContentMd5,
        ContentRange,
        ContentTransferEncoding,
        ContentType,
        Cookie,
        Date,
        Etag,
        Expect,
        Expires,
        Forwarded,
        From,
        Host,
        IfMatch,
        IfModified_since,
        IfNoneMatch,
        IfRange,
        IfUnmodifiedSince,
        LastModified,
        Link,
        Location,
        MaxForwards,
        Origin,
        Pragma,
        ProxyAuthenticate,
        ProxyAuthorization,
        Range,
        Referer,
        RemoteAddress,
        RetryAfter,
        SecWebsocketKey,
        SecWebsocketExtensions,
        SecWebsocketAccept,
        SecWebsocketProtocol,
        SecWebsocketVersion,
        Server,
        SetCookie,
        StrictTransportSecurity,
        Te,
        Trailer,
        TransferEncoding,
        Upgrade,
        Useragent,
        Vary,
        Via,
        Warning,
        WwwAuthenticate,
        XFrameOptions,
        XForwardedFor,
        XForwardedHost,
        XForwardedPort,
        XForwardedProto,
        XRealIp,
        XRequestedWith,
        XXssProtection
      )
