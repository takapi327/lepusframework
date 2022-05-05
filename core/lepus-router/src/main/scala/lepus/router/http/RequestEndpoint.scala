/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import lepus.router.mvc._

object RequestEndpoint {

  sealed trait Endpoint {
    private[lepus] type thisType
    def and(other: Endpoint): Endpoint =
      RequestEndpoint.Pair(this, other)

    def /(other: Path):   Endpoint = and(other)
    def :?(other: Query): Endpoint = and(other)
    def :&(other: Query): Endpoint = and(other)
  }

  sealed trait Param extends Endpoint {
    def converter:   EndpointConverter[String, _]
    def description: Option[String]
    def setDescription(content: String): thisType
  }

  sealed trait Path extends Endpoint {
    def name: String
  }

  sealed trait Query extends Endpoint {
    def key: String
  }

  /** fixed-character path
    *
    * @param name
    *   Path Name
    * @param converter
    *   For converting String paths to any T type
    * @tparam T
    *   Parameters of the type you want to convert String to
    */
  case class FixedPath[T](name: String, converter: EndpointConverter[String, T]) extends Path

  /** Dynamically changing path parameters
    *
    * @param name
    *   Name of path parameter, used for Swagger (Open Api) documentation generation, etc.
    * @param converter
    *   For converting String paths to any T type
    * @tparam T
    *   Parameters of the type you want to convert String to
    */
  case class PathParam[T](name: String, converter: EndpointConverter[String, T], description: Option[String] = None)
    extends Path
       with Param {
    override private[lepus] type thisType = PathParam[T]
    override def setDescription(content: String): PathParam[T] = this.copy(description = Some(content))
    def validate(validator: Validator): Path = ValidatePathParam(name, converter, validator, description)
  }

  /** Query parameter
    *
    * @param key
    *   The key name of the query parameter, which is also used to generate Swagger (Open Api) documentation
    * @param converter
    *   For converting String paths to any T type
    * @tparam T
    *   Parameters of the type you want to convert String to
    */
  case class QueryParam[T](key: String, converter: EndpointConverter[String, T], description: Option[String] = None)
    extends Query
       with Param {
    override private[lepus] type thisType = QueryParam[T]
    override def setDescription(content: String): QueryParam[T] = this.copy(description = Some(content))
    def validate(validator: Validator): Query = ValidateQueryParam(key, converter, validator, description)
  }

  /** Validation defined value for dynamically changing path parameters
    *
    * @param name
    *   Name of path parameter, used for Swagger (Open Api) documentation generation, etc.
    * @param converter
    *   For converting String paths to any T type
    * @param validator
    *   Validation settings to validate path parameters
    * @tparam T
    *   Parameters of the type you want to convert String to
    */
  case class ValidatePathParam[T](
    name:        String,
    converter:   EndpointConverter[String, T],
    validator:   Validator,
    description: Option[String] = None
  ) extends Path
       with Param {
    override private[lepus] type thisType = ValidatePathParam[T]
    override def setDescription(content: String): ValidatePathParam[T] = this.copy(description = Some(content))
  }

  /** Validation defined value for dynamically changing query parameters
    *
    * @param key
    *   The key name of the query parameter, which is also used to generate Swagger (Open Api) documentation
    * @param converter
    *   For converting String paths to any T type
    * @param validator
    *   Validation settings to validate path parameters
    * @tparam T
    *   Parameters of the type you want to convert String to
    */
  case class ValidateQueryParam[T](
    key:         String,
    converter:   EndpointConverter[String, T],
    validator:   Validator,
    description: Option[String] = None
  ) extends Query
       with Param {
    override private[lepus] type thisType = ValidateQueryParam[T]
    override def setDescription(content: String): ValidateQueryParam[T] = this.copy(description = Some(content))
  }

  /** Model to store RequestEndpoint pairs
    *
    * @param left
    *   Pairs of RequestEndpoints are stored from left to right in the HTTP URL path.
    * @param right
    *   The RequestEndpoint currently referenced in the HTTP URL path is stored.
    */
  case class Pair(
    left:  Endpoint,
    right: Endpoint
  ) extends Endpoint
}
