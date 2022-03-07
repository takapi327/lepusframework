/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router

import scala.annotation.tailrec

import http._
import lepus.router.model.{ DecodeResult, HttpServerRequest }

object DecodeEndpoint {

  def apply(
    request:  HttpServerRequest,
    endpoint: RequestEndpoint[_]
  ): (DecodeEndpointResult, DecodeServerRequest) =
    tailrecMatchPath(DecodeServerRequest(request), endpoint.asVector(), Vector.empty)

  @tailrec
  private def tailrecMatchPath(
    request:   DecodeServerRequest,
    endpoints: Vector[RequestEndpoint[_]],
    decoded:   Vector[(RequestEndpoint[_], DecodeResult[_])]
  ): (DecodeEndpointResult, DecodeServerRequest) =
    endpoints.headAndTail match {
      case Some((head, tail)) => {
        head match {
          case RequestEndpoint.FixedPath(name, _) => {
            val (nextSegment, decodeServerRequest) = request.nextPathSegment
            nextSegment match {
              case Some(segment) => {
                if (segment == name) {
                  tailrecMatchPath(decodeServerRequest, tail, decoded)
                } else {
                  val failure = DecodeEndpointResult.MissMatch(head, DecodeResult.Mismatch(segment, name))
                  (failure, decodeServerRequest)
                }
              }
              case None => {
                val failure = DecodeEndpointResult.NoSuchElement(head, DecodeResult.Missing)
                (failure, decodeServerRequest)
              }
            }
          }
          case RequestEndpoint.PathParam(_, converter) => {
            val (nextSegment, decodeServerRequest) = request.nextPathSegment
            nextSegment match {
              case Some(segment) => {
                val newDecoded = decoded :+ ((head, converter.decode(segment)))
                tailrecMatchPath(decodeServerRequest, tail, newDecoded)
              }
              case None => {
                val failure = DecodeEndpointResult.NoSuchElement(head, DecodeResult.Missing)
                (failure, decodeServerRequest)
              }
            }
          }
          case RequestEndpoint.ValidateParam(_, converter, validator) => {
            val (nextSegment, decodeServerRequest) = request.nextPathSegment
            nextSegment match {
              case Some(segment) => {
                validator(segment) match {
                  case Some(decodeResult) => {
                    val failure = DecodeEndpointResult.ValidationError(head, decodeResult)
                    (failure, decodeServerRequest)
                  }
                  case None => {
                    val newDecoded = decoded :+ ((head, converter.decode(segment)))
                    tailrecMatchPath(decodeServerRequest, tail, newDecoded)
                  }
                }
              }
              case None => {
                val failure = DecodeEndpointResult.NoSuchElement(head, DecodeResult.Missing)
                (failure, decodeServerRequest)
              }
            }
          }
          case RequestEndpoint.QueryParam(_, converter) => {
            val (nextSegment, decodeServerRequest) = request.nextPathSegment
            nextSegment match {
              case Some(segment) => {
                val newDecoded = decoded :+ ((head, converter.decode(segment)))
                tailrecMatchPath(decodeServerRequest, tail, newDecoded)
              }
              case None => {
                val failure = DecodeEndpointResult.NoSuchElement(head, DecodeResult.Missing)
                (failure, decodeServerRequest)
              }
            }
          }
          case _ => throw new IllegalStateException("The received value does not match any of the Endpoints.")
        }
      }
      case None => {
        val (nextSegment, decodeServerRequest) = request.nextPathSegment
        nextSegment match {
          case Some(v) => (DecodeEndpointResult.PathMissPatch(v), decodeServerRequest)
          case None    => (tailrecDecode(decoded, DecodeEndpointResult.Success(Vector.empty)), decodeServerRequest)
        }
      }
    }

  @tailrec
  private def tailrecDecode(
    decodedEndpoints: Vector[(RequestEndpoint[_], DecodeResult[_])],
    decodeResult:     DecodeEndpointResult.Success
  ): DecodeEndpointResult = {
    decodedEndpoints.headAndTail match {
      case None               => decodeResult
      case Some((head, tail)) => {
        head match {
          case (endpoint, failure: DecodeResult.Failure) => DecodeEndpointResult.Error(endpoint, failure)
          case (_, DecodeResult.Success(value))          => tailrecDecode(tail, decodeResult.update(value))
        }
      }
    }
  }
}

sealed trait DecodeEndpointResult
object DecodeEndpointResult {
  case class Success(decodedEndpoints: Vector[Any]) extends DecodeEndpointResult {
    def update(decodedEndpoint: Any): Success =
      copy(decodedEndpoints :+ decodedEndpoint)
  }

  sealed trait Failure extends DecodeEndpointResult

  case class Error(endpoint: RequestEndpoint[_], failure: DecodeResult.Failure) extends Failure
  case class MissMatch(endpoint: RequestEndpoint[_], failure: DecodeResult.Failure) extends Failure
  case class NoSuchElement(endpoint: RequestEndpoint[_], failure: DecodeResult.Failure) extends Failure
  case class ValidationError(endpoint: RequestEndpoint[_], failure: DecodeResult.Failure) extends Failure
  case class PathMissPatch(path: String) extends Failure
}


case class DecodeServerRequest(request: HttpServerRequest, pathSegments: List[String]) {
  def nextPathSegment: (Option[String], DecodeServerRequest) =
    pathSegments match {
      case Nil    => (None, this)
      case h :: t => (Some(h), DecodeServerRequest(request, t))
    }
}

object DecodeServerRequest {
  def apply(request: HttpServerRequest): DecodeServerRequest = DecodeServerRequest(request, request.pathSegments)
}
