/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router

import cats.data.{ Kleisli, OptionT }
import cats.implicits.*
import cats.effect.{ Async, Sync }

import org.http4s.{ Request as Request4s, HttpRoutes as Http4sRoutes, Response as Response4s }

import lepus.router.*
import lepus.router.http.*
import lepus.router.internal.*
import lepus.router.ConvertResult.*

/** Compare and verify Http requests and endpoints, and combine them with logic.
 *
 * @tparam F
 *   the effect type.
 */
private[lepus] trait ServerInterpreter[F[_]: Sync: Async]:

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
  def bindFromRequest[T](routes: Requestable[F][T], endpoint: Endpoint[?]): Http4sRoutes[F] =
    Kleisli[[K] =>> OptionT[F, K], Request4s[F], Response4s[F]] { (request4s: Request4s[F]) =>
      for
        decoded  <- OptionT.fromOption[F](decodeRequest[T](Request.fromHttp4s[F](request4s), endpoint))
        logic    <- OptionT.fromOption[F](routes(using decoded)(using request4s).lift(request4s.method))
        response <- OptionT.liftF(logic)
      yield response
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
    request:  Request,
    endpoint: Endpoint[?]
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
