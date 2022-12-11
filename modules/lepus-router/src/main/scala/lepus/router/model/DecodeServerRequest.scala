/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.model

import lepus.router.http.Request

trait DecodeServerRequest

/** A model for performing comparative verification between the path of an Http request and the value of the path that
  * an endpoint has.
  *
  * @param request
  *   Request to pass Http request to wrapped Server
  * @param pathSegments
  *   The value of the Http request path divided by / and stored in an array.
  */
case class DecodePathRequest(request: Request, pathSegments: List[String]) extends DecodeServerRequest:
  def nextPathSegment: (Option[String], DecodePathRequest) =
    pathSegments match
      case Nil          => (None, this)
      case head :: tail => (Some(head), DecodePathRequest(request, tail))

object DecodePathRequest:
  def apply(request: Request): DecodePathRequest = DecodePathRequest(request, request.pathSegments)

/** A model for comparison and verification of the values of query parameters in Http requests and query parameters that
  * endpoints have.
  *
  * @param request
  *   Request to pass Http request to wrapped Server
  * @param querySegments
  *   The value of the query parameter of the Http request stored in Map.
  */
case class DecodeQueryRequest(request: Request, querySegments: Map[String, Seq[String]]) extends DecodeServerRequest:
  def nextQuerySegment(key: String): (Option[Seq[String]], DecodeQueryRequest) =
    querySegments.get(key) match
      case Some(value) => (Some(value.flatMap(_.split(","))), DecodeQueryRequest(request, querySegments - key))
      case None        => (None, this)

object DecodeQueryRequest:
  def apply(request: Request): DecodeQueryRequest = DecodeQueryRequest(request, request.queryParameters)
