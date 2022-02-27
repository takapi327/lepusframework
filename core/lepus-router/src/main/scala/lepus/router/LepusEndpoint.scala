/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router

import scala.reflect._

import cats.effect.{ Async, Sync }

import org.http4s.HttpRoutes

import lepus.router.http._
import lepus.router.model.Endpoint

abstract class LepusEndpoint[F[_], T](
  val endpoint:    RequestEndpoint[_],
  val summary:     Option[String]     = None,
  val description: Option[String]     = None
)(implicit asyncF: Async[F], syncF: Sync[F]) extends Endpoint {

  def toRoutes(routes: Routes[F, T]): ServerRoute[F, T] =
    ServerRoute(this, routes)
}

final case class ServerRoute[F[_], T](
  endpoint: Endpoint,
  pf:       Routes[F, T]
)(implicit asyncF: Async[F], syncF: Sync[F]) {

  val methods: List[RequestMethod] = RequestMethod.all.filter(pf.lift(_).nonEmpty)

  def toHttpRoutes[T](): HttpRoutes[F] =
    ServerInterpreter[F]().bindFromRequest(pf, endpoint.endpoint)
}
