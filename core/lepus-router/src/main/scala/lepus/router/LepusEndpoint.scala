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
import lepus.router.model.{ Endpoint, ServerResponse }

abstract class LepusEndpoint[F[_], M <: RequestMethod, T](
  val endpoint:    RequestEndpoint[_],
  val summary:     Option[String]     = None,
  val description: Option[String]     = None
)(implicit
  classTag: ClassTag[M],
  asyncF:   Async[F],
  syncF:    Sync[F]
) extends Endpoint {
  val method = classTag.runtimeClass.newInstance().asInstanceOf[M]

  private val pf: PartialFunction[String, RequestEndpoint[_]] = {
    case str: String if method.is(str) => endpoint
  }

  def toRoutes(logic: T => F[ServerResponse]): HttpRoutes[F] =
    ServerInterpreter[F]().bindRequest(pf, logic)

  def ->(logic: T => F[ServerResponse]): HttpRoutes[F] = toRoutes(logic)
}
