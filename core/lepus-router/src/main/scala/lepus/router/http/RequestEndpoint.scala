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

  /**
   * fixed-character path
   *
   * @param name      Path Name
   * @param converter For converting String paths to any T type
   * @tparam T        Parameters of the type you want to convert String to
   */
  case class FixedPath[T](name: String, converter: EndpointConverter[String, T]) extends RequestEndpoint[T]

  /**
   * Dynamically changing path parameters
   *
   * @param name      Name of path parameter, used for Swagger (Open Api) documentation generation, etc.
   * @param converter For converting String paths to any T type
   * @tparam T        Parameters of the type you want to convert String to
   */
  case class PathParam[T](name: String, converter: EndpointConverter[String, T]) extends RequestEndpoint[T] {
    def validate(validator: Validator): ValidatePathParam[T] = ValidatePathParam(name, converter, validator)
  }

  /**
   * Query parameter
   *
   * @param key       The key name of the query parameter, which is also used to generate Swagger (Open Api) documentation
   * @param converter For converting String paths to any T type
   * @tparam T        Parameters of the type you want to convert String to
   */
  case class QueryParam[T](key: String, converter: EndpointConverter[String, T]) extends RequestEndpoint[T]

  /**
   * Validation defined value for dynamically changing path parameters
   *
   * @param name      Name of path parameter, used for Swagger (Open Api) documentation generation, etc.
   * @param converter For converting String paths to any T type
   * @param validator Validation settings to validate path parameters
   * @tparam T        Parameters of the type you want to convert String to
   */
  case class ValidatePathParam[T](name: String, converter: EndpointConverter[String, T], validator: Validator) extends RequestEndpoint[T]

  /**
   * Model to store RequestEndpoint pairs
   *
   * @param left  Pairs of RequestEndpoints are stored from left to right in the HTTP URL path.
   * @param right The RequestEndpoint currently referenced in the HTTP URL path is stored.
   * @tparam L    Type parameter to adapt to the path stored in the RequestEndpoint pair
   * @tparam R    Type parameter applied to the path of the currently referenced RequestEndpoint
   * @tparam LR   L, R compound
   */
  case class Pair[L, R, LR](
    left:  RequestEndpoint[L],
    right: RequestEndpoint[R]
  ) extends RequestEndpoint[LR]
}
