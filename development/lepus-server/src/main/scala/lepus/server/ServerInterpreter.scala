/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import cats.data.{ Kleisli, OptionT }
import cats.implicits.*
import cats.effect.{ Async, Sync }

import org.http4s.{ Request as Http4sRequest, HttpRoutes as Http4sRoutes, Response as Http4sResponse }

import lepus.router.*
import lepus.router.http.*
import lepus.router.internal.*
import lepus.router.model.{ ServerRequest, ServerResponse }
import lepus.router.ConvertResult.*

/** Compare and verify Http requests and endpoints, and combine them with logic.
  *
  * @tparam F
  *   the effect type.
  */
trait ServerInterpreter[F[_]](using Sync[F], Async[F]):

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
  def bindFromRequest[T](routes: Requestable[F][T], endpoint: RequestEndpoint.Endpoint): Http4sRoutes[F] =
    Kleisli[[K] =>> OptionT[F, K], Http4sRequest[F], Http4sResponse[F]] { (http4sRequest: Http4sRequest[F]) =>
      val request = Request[F](http4sRequest)

      for
        decoded  <- OptionT.fromOption[F] { decodeRequest[T](request, endpoint) }
        logic    <- OptionT.fromOption[F] { routes(using decoded)(using request).lift(request.method) }
        response <- OptionT.liftF { logic }
      yield addResponseHeader(response).toHttp4sResponse()
    }

  /** Add response headers according to body
    *
    * @param response
    *   Logic return value corresponding to the endpoint
    * @return
    *   ServerResponse with headers according to the contents of the body
    */
  def addResponseHeader(response: ServerResponse): ServerResponse =
    response.body match
      case None => response
      case Some(body) =>
        body match
          case PlainText(_) => response.addHeader(Header.HeaderType.TextPlain)
          case JsValue(_)   => response.addHeader(Header.HeaderType.ApplicationJson)
          case _            => response

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
  ): Option[T] =
    val (decodeEndpointResult, _) = DecodeEndpoint(request, endpoint)
    decodeEndpointResult match
      case _: DecodeEndpointResult.Failure => None
      case DecodeEndpointResult.Success(values) =>
        Some(
          (if values.nonEmpty then values.toTuple
           else {
             // TODO: If there is no value to pass to the logic, Unit is returned, but Nothing can be returned.
           }).asInstanceOf[T]
        )
