/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import scala.annotation.targetName

import org.http4s.Uri

import lepus.router.{ EndpointConverter, Validator }
import lepus.router.internal.ParamConcat

import Endpoint.*
sealed trait Endpoint[T]:
  private[lepus] type TypeParam = T
  private[lepus] type ThisType <: Endpoint[T]
  @targetName("and") def ++[N, TN](other: Endpoint[N])(using ParamConcat.Aux[T, N, TN]): Endpoint[TN] =
    Pair[T, N, TN](this, other)

  @targetName("splitPath") def /[N, TN](path: Path[N])(using ParamConcat.Aux[T, N, TN]): Endpoint[TN] = this ++ path

  @targetName("queryQ") def +?[N, TN](query: Query[N])(using ParamConcat.Aux[T, N, TN]): Endpoint[TN] = this ++ query
  @targetName("query&") def +&[N, TN](query: Query[N])(using ParamConcat.Aux[T, N, TN]): Endpoint[TN] = this ++ query

  /** format method to generate a string of paths. */
  private def formatToString[A](endpoint: Endpoint[A]): String =
    endpoint match
      case Endpoint.Pair(left, right) => formatToString(left) + "/" + formatToString(right)
      case Endpoint.FixedPath(name, _) => name
      case Endpoint.PathParam(_, _, _, _) => "%s"
      case Endpoint.QueryParam(_, _, _, _) => "%s"
      case Endpoint.ValidatePathParam(_, _, _, _, _) => "%s"
      case Endpoint.ValidateQueryParam(_, _, _, _, _) => "%s"

  /** Value for generating a string of paths using the format method. */
  lazy val formatString: String = formatToString(this)

  /** Method for generating [[Uri]] from a format string. */
  def formatToUri(func: String => Uri): Uri = func(formatToString(this))

  /** A method to generate Uri from an unsafe string. */
  lazy val unsafeToUri: Uri = Uri.unsafeFromString(formatToString(this))

  override def toString: String = formatString

object Endpoint:

  /** Model for representing endpoint parameters
    *
    * @tparam T
    *   Type of parameters received from the URL
    */
  sealed trait Param[T] extends Endpoint[T]:
    /** A value to convert from a string to an arbitrary type.
      */
    def converter: EndpointConverter[String, T]

    /** Details of this parameter
      */
    def description: Option[String]

    /** Indicates whether this parameter is required or not.
      */
    def required: Boolean

    /** Update parameter description
      */
    def setDescription(content: String): ThisType

    /** Update parameter required
      */
    def setRequired(bool: Boolean): ThisType

  /** Model for representing endpoint path parameters
    *
    * @tparam T
    *   Type of parameters received from the URL
    */
  sealed trait Path[T] extends Endpoint[T]:
    def name: String

  /** Model for representing endpoint query parameters
    *
    * @tparam T
    *   Type of parameters received from the URL
    */
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
  case class PathParam[T](
    name:        String,
    converter:   EndpointConverter[String, T],
    required:    Boolean        = true,
    description: Option[String] = None
  ) extends Path[T],
            Param[T]:
    override private[lepus] type ThisType = PathParam[T]
    override def setDescription(content: String): PathParam[T] = this.copy(description = Some(content))
    override def setRequired(bool: Boolean):      PathParam[T] = this.copy(required = bool)
    def validate(validator: Validator): Path[T] = ValidatePathParam(name, converter, validator, required, description)

  /** Query parameter
    *
    * @param key
    *   The key name of the query parameter, which is also used to generate Swagger (Open Api) documentation
    * @param converter
    *   For converting String paths to any T type
    * @tparam T
    *   Parameters of the type you want to convert String to
    */
  case class QueryParam[T](
    key:         String,
    converter:   EndpointConverter[String, T],
    required:    Boolean        = false,
    description: Option[String] = None
  ) extends Query[T],
            Param[T]:
    override private[lepus] type ThisType = QueryParam[T]
    override def setDescription(content: String): QueryParam[T] = this.copy(description = Some(content))
    override def setRequired(bool: Boolean):      QueryParam[T] = this.copy(required = bool)
    def validate(validator: Validator): Query[T] = ValidateQueryParam(key, converter, validator, required, description)

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
    required:    Boolean        = true,
    description: Option[String] = None
  ) extends Path[T],
            Param[T]:
    override private[lepus] type ThisType = ValidatePathParam[T]
    override def setDescription(content: String): ValidatePathParam[T] = this.copy(description = Some(content))
    override def setRequired(bool: Boolean):      ValidatePathParam[T] = this.copy(required = bool)

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
    required:    Boolean        = false,
    description: Option[String] = None
  ) extends Query[T],
            Param[T]:
    override private[lepus] type ThisType = ValidateQueryParam[T]
    override def setDescription(content: String): ValidateQueryParam[T] = this.copy(description = Some(content))
    override def setRequired(bool: Boolean):      ValidateQueryParam[T] = this.copy(required = bool)

  /** Model to store Endpoint pairs
    *
    * @param left
    *   Pairs of Endpoints are stored from left to right in the HTTP URL path.
    * @param right
    *   The Endpoint currently referenced in the HTTP URL path is stored.
    */
  case class Pair[T, N, TN](
    left:  Endpoint[T],
    right: Endpoint[N]
  ) extends Endpoint[TN]:
    override private[lepus] type ThisType = Pair[T, N, TN]
