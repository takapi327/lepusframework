/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import scala.annotation.targetName

import org.http4s.{ Method, Request, Response, HttpRoutes as Http4sRoutes }

import lepus.router.RouterConstructor
import lepus.router.http.*

package object router:

  type Http[T] = PartialFunction[Method, T]

  type HttpRoutes[F[_]] = Http[F[Response[F]]]

  type Routing[F[_]]     = (Endpoint[?], RouterConstructor[F, ?])
  type Requestable[F[_]] = [T] =>> T ?=> Request[F] ?=> HttpRoutes[F]

  /** Implicit value to convert String type to Endpoint.FixedPath
   *
   * @return
   * [[lepus.router.http.Endpoint.FixedPath]]
   */
  given Conversion[String, Endpoint.FixedPath[Unit]] =
    v => Endpoint.FixedPath(v, summon)

  /** Implicit value to convert Int type to Endpoint.FixedPath
   *
   * @return
   * [[lepus.router.http.Endpoint.FixedPath]]
   */
  given Conversion[Int, Endpoint.FixedPath[Unit]] =
    v => Endpoint.FixedPath(v.toString, summon)

  /** Implicit value to convert Long type to Endpoint.FixedPath
   *
   * @return
   * [[lepus.router.http.Endpoint.FixedPath]]
   */
  given Conversion[Long, Endpoint.FixedPath[Unit]] =
    v => Endpoint.FixedPath(v.toString, summon)

  /** Implicit value to convert Short type to Endpoint.FixedPath
   *
   * @return
   * [[lepus.router.http.Endpoint.FixedPath]]
   */
  given Conversion[Short, Endpoint.FixedPath[Unit]] =
    v => Endpoint.FixedPath(v.toString, summon)

  /** Method to generate Endpoint.PathParam with Name
   *
   * @param name
   * Name of path parameter, used for Open Api documentation generation, etc.
   * @param EndpointConverter
   * A value to convert from a string to an arbitrary type.
   * @tparam T
   * Type of parameters received from the URL
   * @return
   * [[lepus.router.http.Endpoint.PathParam]]
   */
  def bindPath[T](name: String)(using EndpointConverter[String, T]): Endpoint.PathParam[T] =
    Endpoint.PathParam(name, summon)

  /** Method to generate Endpoint.PathParam with all parameters
   *
   * @param name
   * Name of path parameter, used for Open Api documentation generation, etc.
   * @param required
   * Value indicating whether this path parameter is a required value
   * @param description
   * Description of this path parameter
   * @param EndpointConverter
   * A value to convert from a string to an arbitrary type.
   * @tparam T
   * Type of parameters received from the URL
   * @return
   * [[lepus.router.http.Endpoint.PathParam]]
   */
  def bindPath[T](name: String, required: Boolean, description: String)(using
    EndpointConverter[String, T]
  ): Endpoint.PathParam[T] =
    Endpoint.PathParam(name, summon, required, Some(description))

  /** Method to generate Endpoint.QueryParam with Key
   *
   * @param key
   * The key name of the query parameter, which is also used to generate Open Api documentation
   * @param EndpointConverter
   * A value to convert from a string to an arbitrary type.
   * @tparam T
   * Type of parameters received from the URL
   * @return
   * [[lepus.router.http.Endpoint.QueryParam]]
   */
  def bindQuery[T](key: String)(using EndpointConverter[String, T]): Endpoint.QueryParam[T] =
    Endpoint.QueryParam(key, summon)

  /** Method to generate Endpoint.QueryParam with all parameters
   *
   * @param key
   * The key name of the query parameter, which is also used to generate Open Api documentation
   * @param required
   * Value indicating whether this query parameter is a required value
   * @param description
   * Description of this query parameter
   * @param EndpointConverter
   * A value to convert from a string to an arbitrary type.
   * @tparam T
   * Type of parameters received from the URL
   * @return
   * [[lepus.router.http.Endpoint.QueryParam]]
   */
  def bindQuery[T](key: String, required: Boolean, description: String)(using
    EndpointConverter[String, T]
  ): Endpoint.QueryParam[T] =
    Endpoint.QueryParam(key, summon, required, Some(description))

  /** Extension methods for type-safe generation of Endpoint and RouterConstructor Tuple
   */
  extension[F[_], T] (endpoint: Endpoint[T])
    @targetName("toTuple") def ->>(router: RouterConstructor[F, endpoint.TypeParam]) = (endpoint, router)
