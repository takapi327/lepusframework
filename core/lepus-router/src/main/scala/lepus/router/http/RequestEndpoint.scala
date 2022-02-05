/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.http

sealed trait RequestEndpoint[T] {

  def and[T](other: RequestEndpoint[T]): RequestEndpoint[T] =
    RequestEndpoint.Pair(this, other)

  def /[T](other: RequestEndpoint[T]): RequestEndpoint[T] = and(other)

  def :?[T](other: RequestEndpoint.QueryParam[T]): RequestEndpoint[T] = and(other)
  def :&[T](other: RequestEndpoint.QueryParam[T]): RequestEndpoint[T] = and(other)
}

object RequestEndpoint {

  case class FixedPath[T](name: String, converter: EndpointConverter[String, T]) extends RequestEndpoint[T]
  case class AnyPath[T](name: Option[String], converter: EndpointConverter[String, T]) extends RequestEndpoint[T]
  case class QueryParam[T](key: String, converter: EndpointConverter[String, T]) extends RequestEndpoint[T]

  case class Pair[L, R, LR](
    left:  RequestEndpoint[L],
    right: RequestEndpoint[R]
  ) extends RequestEndpoint[LR]
}
