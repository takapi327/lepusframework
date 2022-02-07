/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router

import cats.data.{ Kleisli, OptionT }
import cats.implicits._

import cats.effect.{ Async, Sync }

import org.http4s._

trait ServerInterpreter[F[_]] {

  implicit def syncF:  Sync[F]
  implicit def asyncF: Async[F]

  def bindRequest[T](endpoint: LepusEndpoint[F, _, T], logic: T => F[http.ServerResponse]): HttpRoutes[F] = {
    Kleisli { (request: Request[F]) =>
      val serverRequest = new ServerRequest[F](request)

      for {
        decodeResult <- OptionT.fromOption[F] { RequestDecodeHandler.handleRequest(serverRequest, endpoint) match {
                          case _: DecodeRequestResult.Failure        => None
                          case DecodeRequestResult.Success(response) => Some(response)
                        }}
        response     <- OptionT { logic(decodeResult.toTuple.asInstanceOf[T]).map(_.toHttp4sResponse[F]()).map(_.some) }
      } yield response
    }
  }
}

object ServerInterpreter {
  def apply[F[_]]()(implicit
    _asyncF: Async[F],
    _syncF:  Sync[F]
  ): ServerInterpreter[F] = {
    new ServerInterpreter[F] {
      override implicit def syncF:  Sync[F]  = _syncF
      override implicit def asyncF: Async[F] = _asyncF
    }
  }
}
