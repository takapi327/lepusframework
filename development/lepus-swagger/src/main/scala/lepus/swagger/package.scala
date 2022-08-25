/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import scala.annotation.targetName

import org.http4s.Method

import lepus.router.http.RequestEndpoint
import lepus.router.RouterConstructor

package object swagger extends ExtensionMethods:

  type RouteApi[F[_]] = (RequestEndpoint.Endpoint[?], OpenApiConstructor[F, ?])

  extension [F[_], T](endpoint: RequestEndpoint.Endpoint[T])
    @targetName("toTuple") def ->(
      router: OpenApiConstructor[F, endpoint.TypeParam]
    ) = (endpoint, router)

  val allMethods: List[Method] =
    List(
      Method.ACL,
      Method.`BASELINE-CONTROL`,
      Method.GET,
      Method.BIND,
      Method.CHECKIN,
      Method.CHECKOUT,
      Method.CONNECT,
      Method.COPY,
      Method.DELETE,
      Method.GET,
      Method.HEAD,
      Method.LABEL,
      Method.LINK,
      Method.LOCK,
      Method.MERGE,
      Method.MKACTIVITY,
      Method.MKCALENDAR,
      Method.MKCOL,
      Method.MKREDIRECTREF,
      Method.MKWORKSPACE,
      Method.MOVE,
      Method.OPTIONS,
      Method.ORDERPATCH,
      Method.PATCH,
      Method.POST,
      Method.PRI,
      Method.PROPFIND,
      Method.PROPPATCH,
      Method.PUT,
      Method.REBIND,
      Method.REPORT,
      Method.SEARCH,
      Method.TRACE,
      Method.UNBIND,
      Method.UNCHECKOUT,
      Method.UNLINK,
      Method.UNLOCK,
      Method.UPDATE,
      Method.UPDATEREDIRECTREF,
      Method.`VERSION-CONTROL`
    )
