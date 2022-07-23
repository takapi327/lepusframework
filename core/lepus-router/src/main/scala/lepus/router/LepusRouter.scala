/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import lepus.router.http.*

trait LepusRouter:

  given Conversion[String, RequestEndpoint.FixedPath[String]] =
    v => RequestEndpoint.FixedPath(v, EndpointConverter.string)

  given Conversion[Int, RequestEndpoint.FixedPath[String]] =
    v => RequestEndpoint.FixedPath(v.toString, EndpointConverter.string)

  given Conversion[Long, RequestEndpoint.FixedPath[String]] =
    v => RequestEndpoint.FixedPath(v.toString, EndpointConverter.string)

  given Conversion[Short, RequestEndpoint.FixedPath[String]] =
    v => RequestEndpoint.FixedPath(v.toString, EndpointConverter.string)

  def bindPath[T](name: String)(using EndpointConverter[String, T]): RequestEndpoint.PathParam[T] =
    RequestEndpoint.PathParam(name, summon)

  def bindQuery[T](key: String)(using EndpointConverter[String, T]): RequestEndpoint.QueryParam[T] =
    RequestEndpoint.QueryParam(key, summon)
