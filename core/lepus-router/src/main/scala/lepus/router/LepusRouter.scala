/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import lepus.router.http._
import lepus.router.mvc.EndpointConverter

trait LepusRouter {
  implicit def stringToPath(str: String): RequestEndpoint.FixedPath[String] =
    RequestEndpoint.FixedPath(str, EndpointConverter.string)

  implicit def intToPath(int: Int): RequestEndpoint.FixedPath[String] =
    RequestEndpoint.FixedPath(int.toString, EndpointConverter.string)

  implicit def longToPath(long: Long): RequestEndpoint.FixedPath[String] =
    RequestEndpoint.FixedPath(long.toString, EndpointConverter.string)

  implicit def shortToPath(short: Short): RequestEndpoint.FixedPath[String] =
    RequestEndpoint.FixedPath(short.toString, EndpointConverter.string)

  def bindPath[T](name: String)(implicit converter: EndpointConverter[String, T]): RequestEndpoint.PathParam[T] =
    RequestEndpoint.PathParam(name, implicitly)

  def bindQuery[T](key: String)(implicit converter: EndpointConverter[String, T]): RequestEndpoint.QueryParam[T] =
    RequestEndpoint.QueryParam(key, implicitly)
}
