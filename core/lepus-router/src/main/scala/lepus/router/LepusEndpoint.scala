/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router

import cats.effect.{ Async, Sync }

import org.http4s.HttpRoutes

import lepus.router.http._
import lepus.router.model.Endpoint

/**
 * Class for generating endpoints.
 *
 * For example:
 * {{{
 *   // http:localhost:5555/hello/world/lepus
 *   object helloEndpoint extends LepusEndpoint[IO, (String, String)](
 *     endpoint = "hello" / bindPath[String]("world") / bindPath[String]("name"),
 *     summary  = Some("Hello World!")
 *   )
 * }}}
 *
 * @param endpoint    The value that will be the path of the Http request.
 * @param summary     Endpoint Summary Used in Swagger (Open API) documentation.
 * @param description Endpoint Description Used in Swagger (Open API) documentation.
 * @tparam F          the effect type.
 * @tparam T          Type of variable used as a path parameter. It is displayed as a tuple.
 */
abstract class LepusEndpoint[F[_], T](
  val endpoint:    RequestEndpoint[_],
  val summary:     Option[String]     = None,
  val description: Option[String]     = None
)(implicit asyncF: Async[F], syncF: Sync[F]) extends Endpoint {

  /**
   * For example:
   * {{{
   *   helloEndpoint toRoutes {
   *     case GET  => param => IO(...)
   *     case POST => param => IO(...)
   *   }
   * }}}
   *
   * @param routes
   * @return
   */
  def toRoutes(routes: Routes[F, T]): ServerRoute[F, T] =
    ServerRoute(this, routes)
}

/**
 * Class for binding endpoints and logic.
 *
 * @param endpoint The value that will be the path of the Http request.
 * @param routes   Logic part according to endpoints.
 * @tparam F       the effect type.
 * @tparam T       Type of variable used as a path parameter. It is displayed as a tuple.
 */
private[lepus] final case class ServerRoute[F[_], T](
  endpoint: Endpoint,
  routes:   Routes[F, T]
)(implicit asyncF: Async[F], syncF: Sync[F]) {

  val methods: List[RequestMethod] = RequestMethod.all.filter(routes.isDefinedAt)

  def toHttpRoutes(): HttpRoutes[F] =
    ServerInterpreter[F]().bindFromRequest[T](routes, endpoint.endpoint)
}
