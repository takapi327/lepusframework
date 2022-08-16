/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import scala.annotation.targetName

import lepus.router.http.*

trait LepusRouter:

  given Conversion[String, RequestEndpoint.FixedPath[Unit]] =
    v => RequestEndpoint.FixedPath(v, summon)

  given Conversion[Int, RequestEndpoint.FixedPath[Unit]] =
    v => RequestEndpoint.FixedPath(v.toString, summon)

  given Conversion[Long, RequestEndpoint.FixedPath[Unit]] =
    v => RequestEndpoint.FixedPath(v.toString, summon)

  given Conversion[Short, RequestEndpoint.FixedPath[Unit]] =
    v => RequestEndpoint.FixedPath(v.toString, summon)

  def bindPath[T](name: String)(using EndpointConverter[String, T]): RequestEndpoint.PathParam[T] =
    RequestEndpoint.PathParam(name, summon)

  def bindQuery[T](key: String)(using EndpointConverter[String, T]): RequestEndpoint.QueryParam[T] =
    RequestEndpoint.QueryParam(key, summon)

  extension [T] (endpoint: RequestEndpoint.Endpoint[T])
    @targetName("toTuple") def ->[F[_]](router: RouterConstructor[F, endpoint.TypeParam]) = (endpoint, router)
