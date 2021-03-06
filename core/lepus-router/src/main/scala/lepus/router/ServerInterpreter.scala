/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import cats.data.{ Kleisli, OptionT }
import cats.implicits._
import cats.effect.{ Async, Sync }

import org.http4s.{ Request => Http4sRequest, HttpRoutes => Http4sRoutes }

import lepus.router.http._
import lepus.router.internal._
import lepus.router.model.{ ServerRequest, ServerResponse }
import ConvertResult._

/** Compare and verify Http requests and endpoints, and combine them with logic.
  *
  * @tparam F
  *   the effect type.
  */
trait ServerInterpreter[F[_]] {

  implicit def syncF:  Sync[F]
  implicit def asyncF: Async[F]

  /** Receives HTTP requests, compares and verifies them with endpoints, and binds them to server logic.
    *
    * @param routes
    *   Server logic for each HTTP method
    * @param endpoint
    *   Endpoints you expect to receive as requests
    * @tparam T
    *   Type of parameters to be received in the request
    * @return
    *   If the request and endpoint match, http4s HttpRoutes are returned and the server logic is executed.
    */
  def bindFromRequest[T](routes: HttpRoutes[F, T], endpoint: RequestEndpoint.Endpoint): Http4sRoutes[F] = {
    Kleisli { (http4sRequest: Http4sRequest[F]) =>
      val request = new Request[F](http4sRequest)

      for {
        logic        <- OptionT.fromOption[F] { routes.lift(request.method) }
        decodeResult <- OptionT.fromOption[F] { decodeRequest[T](request, endpoint) }
        response     <- OptionT { logic(new ServerRequest[F, T](http4sRequest, decodeResult)).map(_.some) }
      } yield addResponseHeader(response).toHttp4sResponse()
    }
  }

  /** Add response headers according to body
    *
    * @param response
    *   Logic return value corresponding to the endpoint
    * @return
    *   ServerResponse with headers according to the contents of the body
    */
  def addResponseHeader(response: ServerResponse): ServerResponse =
    response.body match {
      case None => response
      case Some(body) =>
        body match {
          case PlainText(_) => response.addHeader(Header.ResponseHeader.TextPlain)
          case JsValue(_)   => response.addHeader(Header.ResponseHeader.ApplicationJson)
          case _            => response
        }
    }

  /** Verify that the actual request matches the endpoint that was intended to be received as a request.
    *
    * @param request
    *   HTTP request for http4s to pass to Server
    * @param endpoint
    *   Endpoints you expect to receive as requests
    * @tparam T
    *   Type of parameters to be received in the request
    * @return
    *   If the request and endpoint match, return Some; if not, return None.
    */
  private def decodeRequest[T](
    request:  HttpRequest,
    endpoint: RequestEndpoint.Endpoint
  ): Option[T] = {
    val (decodeEndpointResult, _) = DecodeEndpoint(request, endpoint)
    decodeEndpointResult match {
      case _: DecodeEndpointResult.Failure => None
      case DecodeEndpointResult.Success(values) =>
        Some((values.nonEmpty match {
          case true => values.toTuple
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
