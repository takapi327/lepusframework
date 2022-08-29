/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import scala.annotation.tailrec

import lepus.router.http.*
import lepus.router.model.*
import lepus.router.internal.*

object DecodeEndpoint:

  type DecodedResult = Vector[(Endpoint[?], DecodeResult[?])]
  type Result        = (DecodeEndpointResult.Success, DecodedResult) => (DecodeEndpointResult, DecodedResult)

  def apply(
    request:  Request,
    endpoint: Endpoint[?]
  ): (DecodeEndpointResult, DecodedResult) =

    val endpoints = endpoint.asVector()

    val endpointPaths       = endpoints.filter(_.isPath)
    val endpointQueryParams = endpoints.filter(_.isQueryParam)

    decodingConvolution(
      tailrecMatchPath(DecodePathRequest(request), endpointPaths, _, _),
      tailrecMatchQuery(DecodeQueryRequest(request), endpointQueryParams, _, _)
    )(DecodeEndpointResult.Success(Vector.empty), Vector.empty) match
      case (result: DecodeEndpointResult.Success, decoded) => (tailrecDecode(decoded, result), decoded)
      case (result, decoded)                               => (result, decoded)

  /** Compares and verifies the path of the Http request with the path of the endpoint, and performs all tail recursion
    * of decoding.
    *
    * @param request
    *   Request to pass Http request to wrapped Server
    * @param endpoints
    *   Array of Vectors with endpoints divided by path parameters
    * @param result
    *   The result of decoding the endpoint is stored.
    * @param decoded
    *   Parameter to store the decoded value of the endpoint
    * @return
    *   Decode all endpoints and return the Http request corresponding to the endpoint
    */
  @tailrec private def tailrecMatchPath(
    request:   DecodePathRequest,
    endpoints: Vector[Endpoint[?]],
    result:    DecodeEndpointResult.Success,
    decoded:   DecodedResult
  ): (DecodeEndpointResult, DecodedResult) =
    endpoints.headAndTail match
      case Some((head, tail)) =>
        head match
          case Endpoint.FixedPath(name, _) =>
            val (nextSegment, decodeServerRequest) = request.nextPathSegment
            nextSegment match
              case Some(segment) =>
                if segment == name then tailrecMatchPath(decodeServerRequest, tail, result, decoded)
                else
                  val failure = DecodeEndpointResult.MissMatch(head, DecodeResult.Mismatch(segment, name))
                  (failure, decoded)
              case None =>
                val failure = DecodeEndpointResult.NoSuchElement(head, DecodeResult.Missing)
                (failure, decoded)
          case Endpoint.PathParam(_, converter, _) =>
            val (nextSegment, decodeServerRequest) = request.nextPathSegment
            nextSegment match
              case Some(segment) =>
                val newDecoded = decoded :+ ((head, converter.decode(segment)))
                tailrecMatchPath(decodeServerRequest, tail, result, newDecoded)
              case None =>
                val failure = DecodeEndpointResult.NoSuchElement(head, DecodeResult.Missing)
                (failure, decoded)
          case Endpoint.ValidatePathParam(_, converter, validator, _) =>
            val (nextSegment, decodeServerRequest) = request.nextPathSegment
            nextSegment match
              case Some(segment) =>
                validator(segment) match
                  case Some(decodeResult) =>
                    val failure = DecodeEndpointResult.ValidationError(head, decodeResult)
                    (failure, decoded)
                  case None =>
                    val newDecoded = decoded :+ ((head, converter.decode(segment)))
                    tailrecMatchPath(decodeServerRequest, tail, result, newDecoded)
              case None =>
                val failure = DecodeEndpointResult.NoSuchElement(head, DecodeResult.Missing)
                (failure, decoded)
          case _ => throw new IllegalStateException("The received value does not match any of the Endpoints.")
      case None =>
        val (nextSegment, _) = request.nextPathSegment
        nextSegment match
          case Some(v) => (DecodeEndpointResult.PathMissPatch(v), decoded)
          case None    => (result, decoded)

  /** Compares and verifies the path of the Http request with the query param of the endpoint, and performs all tail
    * recursion of decoding.
    *
    * @param request
    *   Request to pass Http request to wrapped Server
    * @param endpoints
    *   Array of Vectors with endpoints divided by path parameters
    * @param result
    *   The result of decoding the endpoint is stored.
    * @param decoded
    *   Parameter to store the decoded value of the endpoint
    * @return
    *   Decode all endpoints and return the Http request corresponding to the endpoint
    */
  @tailrec private def tailrecMatchQuery(
    request:   DecodeQueryRequest,
    endpoints: Vector[Endpoint[?]],
    result:    DecodeEndpointResult.Success,
    decoded:   DecodedResult
  ): (DecodeEndpointResult, DecodedResult) =
    endpoints.headAndTail match
      case Some((head, tail)) =>
        head match
          case Endpoint.QueryParam(key, converter, _) =>
            request.nextQuerySegment(key) match
              case (Some(values), decodeServerRequest) =>
                val newDecoded = decoded :+ ((head, converter.decode(values.mkString(","))))
                tailrecMatchQuery(decodeServerRequest, tail, result, newDecoded)
              case _ => (DecodeEndpointResult.NoSuchElement(head, DecodeResult.Missing), decoded)
          case Endpoint.ValidateQueryParam(key, converter, validator, _) =>
            request.nextQuerySegment(key) match
              case (Some(values), decodeServerRequest) =>
                values.flatMap(validator(_)) match
                  case Nil =>
                    val newDecoded = decoded :+ ((head, converter.decode(values.mkString(","))))
                    tailrecMatchQuery(decodeServerRequest, tail, result, newDecoded)
                  case decodeResults =>
                    val failure = DecodeEndpointResult.ValidationError(head, decodeResults.head)
                    (failure, decoded)
              case _ => (DecodeEndpointResult.NoSuchElement(head, DecodeResult.Missing), decoded)
          case _ => throw new IllegalStateException("The received value does not match any of the Endpoints.")
      case None => (result, decoded)

  /** Process with tail recursion so that only Success decoded endpoints are processed.
    *
    * @param decodedEndpoints
    *   Array of decoded endpoints and Http request tuples
    * @param decodeResult
    *   Initial value of the result of decoding the endpoint
    * @return
    *   Value resulting from decoding the endpoint
    */
  @tailrec private def tailrecDecode(
    decodedEndpoints: Vector[(Endpoint[?], DecodeResult[?])],
    decodeResult:     DecodeEndpointResult.Success
  ): DecodeEndpointResult = {
    decodedEndpoints.headAndTail match
      case None => decodeResult
      case Some((head, tail)) =>
        head match
          case (endpoint, failure: DecodeResult.Failure) => DecodeEndpointResult.Error(endpoint, failure)
          case (_, DecodeResult.Success(value))          => tailrecDecode(tail, decodeResult.update(value))
  }

  /** Receive and in turn process decoding endpoints. The path and query parameter decoding processes can be combined by
    * having each decoded result take over and be processed.
    *
    * @param results
    *   The process of decoding path and query parameter endpoints.
    * @return
    *   Result of decoding the path, query parameter endpoints.
    */
  private def decodingConvolution(results: Result*): Result = (result, decoded) =>
    results match
      case head +: tail =>
        head(result, decoded) match
          case (result: DecodeEndpointResult.Success, decoded) => decodingConvolution(tail: _*)(result, decoded)
          case decodedResult                                   => decodedResult
      case _ => (result, decoded)

sealed trait DecodeEndpointResult
object DecodeEndpointResult:
  case class Success(decodedEndpoints: Vector[Any]) extends DecodeEndpointResult:
    def update(decodedEndpoint: Any): Success =
      copy(decodedEndpoints :+ decodedEndpoint)

  sealed trait Failure extends DecodeEndpointResult

  case class Error(endpoint: Endpoint[?], failure: DecodeResult.Failure)           extends Failure
  case class MissMatch(endpoint: Endpoint[?], failure: DecodeResult.Failure)       extends Failure
  case class NoSuchElement(endpoint: Endpoint[?], failure: DecodeResult.Failure)   extends Failure
  case class ValidationError(endpoint: Endpoint[?], failure: DecodeResult.Failure) extends Failure
  case class PathMissPatch(path: String)                                           extends Failure
