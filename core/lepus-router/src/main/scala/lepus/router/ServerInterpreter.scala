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

import lepus.router.http._
import lepus.router.model.ServerRequest

/**
 * Compare and verify Http requests and endpoints, and combine them with logic.
 *
 * @tparam F the effect type.
 */
trait ServerInterpreter[F[_]] {

  implicit def syncF:  Sync[F]
  implicit def asyncF: Async[F]

  /**
   * Receives HTTP requests, compares and verifies them with endpoints,
   * and binds them to server logic.
   *
   * @param routes   Server logic for each HTTP method
   * @param endpoint Endpoints you expect to receive as requests
   * @tparam T       Type of parameters to be received in the request
   * @return         If the request and endpoint match, http4s HttpRoutes are returned and the server logic is executed.
   */
  def bindFromRequest[T](routes: Routes[F, T], endpoint: RequestEndpoint[_]): HttpRoutes[F] = {
    Kleisli { (request: Request[F]) =>
      val serverRequest = new ServerRequest[F](request)

      for {
        logic        <- OptionT.fromOption[F] { routes.lift(serverRequest.method) }
        decodeResult <- OptionT.fromOption[F] { decodeRequest[T](serverRequest, endpoint) }
        response     <- OptionT { logic(decodeResult).map(_.toHttp4sResponse[F]()).map(_.some) }
      } yield response
    }
  }

  /**
   * Verify that the actual request matches the endpoint that was intended to be received as a request.
   *
   * @param serverRequest Wrapped HTTP request for http4s to pass to Server
   * @param endpoint      Endpoints you expect to receive as requests
   * @tparam T            Type of parameters to be received in the request
   * @return              If the request and endpoint match, return Some; if not, return None.
   */
  private def decodeRequest[T](
    serverRequest: ServerRequest[F],
    endpoint:      RequestEndpoint[_]
  ): Option[T] = {
    val (decodeEndpointResult, _) = DecodeEndpoint(serverRequest, endpoint)
    decodeEndpointResult match {
      case _: DecodeEndpointResult.Failure      => None
      case DecodeEndpointResult.Success(values) =>
        Some((values.nonEmpty match {
          case true  => values.toTuple
          case false => // TODO: If there is no value to pass to the logic, Unit is returned, but Nothing can be returned.
        }).asInstanceOf[T])
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
