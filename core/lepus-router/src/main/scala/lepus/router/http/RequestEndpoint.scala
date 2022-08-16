/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import scala.annotation.targetName

import lepus.router.{ EndpointConverter, Validator }
import lepus.router.internal.ParamConcat

object RequestEndpoint:

  sealed trait Endpoint[T]:
    private[lepus] type TypeParam = T
    private[lepus] type ThisType <: Endpoint[T]
    @targetName("and") def ++[N, TN](other: Endpoint[N]): Endpoint[TN] =
      RequestEndpoint.Pair[T, N, TN](this, other)

    @targetName("splitPath") def /[N, TN](path: Path[N])(using ParamConcat.Aux[T, N, TN]): Endpoint[TN] = this ++ path

    @targetName("queryQ") def :?[N, TN](query: Query[N])(using ParamConcat.Aux[T, N, TN]): Endpoint[TN] = this ++ query
    @targetName("query&") def :&[N, TN](query: Query[N])(using ParamConcat.Aux[T, N, TN]): Endpoint[TN] = this ++ query

  sealed trait Param[T] extends Endpoint[T]:
    def converter:   EndpointConverter[String, T]
    def description: Option[String]
    def setDescription(content: String): ThisType

  sealed trait Path[T] extends Endpoint[T]:
    def name: String

  sealed trait Query[T] extends Endpoint[T]:
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
  case class FixedPath[T](name: String, converter: EndpointConverter[String, T]) extends Path[T]

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
    extends Path[T],
      Param[T]:
    override private[lepus] type ThisType = PathParam[T]
    override def setDescription(content: String): PathParam[T] = this.copy(description = Some(content))
    def validate(validator: Validator): Path[T] = ValidatePathParam(name, converter, validator, description)

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
    extends Query[T],
      Param[T]:
    override private[lepus] type ThisType = QueryParam[T]
    override def setDescription(content: String): QueryParam[T] = this.copy(description = Some(content))
    def validate(validator: Validator): Query[T] = ValidateQueryParam(key, converter, validator, description)

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
  ) extends Path[T],
      Param[T]:
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
  ) extends Query[T],
      Param[T]:
    override private[lepus] type ThisType = ValidateQueryParam[T]
    override def setDescription(content: String): ValidateQueryParam[T] = this.copy(description = Some(content))

  /** Model to store RequestEndpoint pairs
    *
    * @param left
    *   Pairs of RequestEndpoints are stored from left to right in the HTTP URL path.
    * @param right
    *   The RequestEndpoint currently referenced in the HTTP URL path is stored.
    */
  case class Pair[T, N, TN](
    left:  Endpoint[T],
    right: Endpoint[N]
  ) extends Endpoint[TN]:
    override private[lepus] type ThisType = Pair[T, N, TN]

end RequestEndpoint
