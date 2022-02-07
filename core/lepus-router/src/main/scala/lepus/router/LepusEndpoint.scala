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

abstract class LepusEndpoint[F[_], M <: RequestMethod, T](
  val endpoint: RequestEndpoint[_]
)(implicit
  classTag: ClassTag[M],
  asyncF:   Async[F],
  syncF:    Sync[F]
) {
  val method = classTag.runtimeClass.newInstance().asInstanceOf[M]

  def toRoutes(logic: T => F[ServerResponse]): HttpRoutes[F] =
    ServerInterpreter[F]().bindRequest(this, logic)

  def ->(logic: T => F[ServerResponse]): HttpRoutes[F] = toRoutes(logic)
}
