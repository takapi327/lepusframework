/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import scala.annotation.targetName

import lepus.router.{ Route, EndpointConverter, Validator, RouterConstructor }

object RequestEndpoint:

  sealed trait Endpoint:
    private[lepus] type ThisType
    @targetName("and") def ++(other: Endpoint): Endpoint =
      RequestEndpoint.Pair(this, other)

    @targetName("addPath") def /(path: Path): Endpoint = this ++ path

    @targetName("addQuery?") def :?(query: Query): Endpoint = this ++ query
    @targetName("addQuery&") def :&(query: Query): Endpoint = this ++ query

    @targetName("endpointToTuple") def ->[F[_]](
      const: RouterConstructor[F, ?]
    ): Route[F] = (this, const)

  sealed trait Param extends Endpoint:
    def converter:   EndpointConverter[String, ?]
    def description: Option[String]
    def setDescription(content: String): ThisType

  sealed trait Path extends Endpoint:
    def name: String

  sealed trait Query extends Endpoint:
    def key: String

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
    extends Path,
      Param:
    override private[lepus] type ThisType = PathParam[T]
    override def setDescription(content: String): PathParam[T] = this.copy(description = Some(content))
    def validate(validator: Validator): Path = ValidatePathParam(name, converter, validator, description)

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
    extends Query,
      Param:
    override private[lepus] type ThisType = QueryParam[T]
    override def setDescription(content: String): QueryParam[T] = this.copy(description = Some(content))
    def validate(validator: Validator): Query = ValidateQueryParam(key, converter, validator, description)

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
  ) extends Path,
      Param:
    override private[lepus] type ThisType = ValidatePathParam[T]
    override def setDescription(content: String): ValidatePathParam[T] = this.copy(description = Some(content))

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
  ) extends Query,
      Param:
    override private[lepus] type ThisType = ValidateQueryParam[T]
    override def setDescription(content: String): ValidateQueryParam[T] = this.copy(description = Some(content))

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

end RequestEndpoint
