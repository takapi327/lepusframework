/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.http

import lepus.router.mvc._

sealed trait RequestEndpoint[T] {

  def and[T](other: RequestEndpoint[T]): RequestEndpoint[T] =
    RequestEndpoint.Pair(this, other)

  def /[T](other: RequestEndpoint[T]): RequestEndpoint[T] = and(other)

  def :?[T](other: RequestEndpoint.QueryParam[T]): RequestEndpoint[T] = and(other)
  def :&[T](other: RequestEndpoint.QueryParam[T]): RequestEndpoint[T] = and(other)
}

object RequestEndpoint {

  case class FixedPath[T](name: String, converter: EndpointConverter[String, T]) extends RequestEndpoint[T]
  case class PathParam[T](name: String, converter: EndpointConverter[String, T]) extends RequestEndpoint[T] {
    def validate(validator: Validator): ValidateParam[T] = ValidateParam(name, converter, validator)
  }
  case class QueryParam[T](key: String, converter: EndpointConverter[String, T]) extends RequestEndpoint[T]

  case class ValidateParam[T](name: String, converter: EndpointConverter[String, T], validator: Validator) extends RequestEndpoint[T]

  case class Pair[L, R, LR](
    left:  RequestEndpoint[L],
    right: RequestEndpoint[R]
  ) extends RequestEndpoint[LR]
}
