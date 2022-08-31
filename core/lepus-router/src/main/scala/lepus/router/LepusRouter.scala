/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import scala.annotation.targetName

import lepus.router.http.*

trait LepusRouter:

  given Conversion[String, Endpoint.FixedPath[Unit]] =
    v => Endpoint.FixedPath(v, summon)

  given Conversion[Int, Endpoint.FixedPath[Unit]] =
    v => Endpoint.FixedPath(v.toString, summon)

  given Conversion[Long, Endpoint.FixedPath[Unit]] =
    v => Endpoint.FixedPath(v.toString, summon)

  given Conversion[Short, Endpoint.FixedPath[Unit]] =
    v => Endpoint.FixedPath(v.toString, summon)

  def bindPath[T](name: String)(using EndpointConverter[String, T]): Endpoint.PathParam[T] =
    Endpoint.PathParam(name, summon)

  def bindPath[T](name: String, required: Boolean, description: String)(using EndpointConverter[String, T]): Endpoint.PathParam[T] =
    Endpoint.PathParam(name, summon, required, Some(description))

  def bindQuery[T](key: String)(using EndpointConverter[String, T]): Endpoint.QueryParam[T] =
    Endpoint.QueryParam(key, summon)

  def bindQuery[T](key: String, required: Boolean, description: String)(using EndpointConverter[String, T]): Endpoint.QueryParam[T] =
    Endpoint.QueryParam(key, summon, required, Some(description))

  extension [F[_], T](endpoint: Endpoint[T])
    @targetName("toTuple") def ->>(router: RouterConstructor[F, endpoint.TypeParam]) = (endpoint, router)
