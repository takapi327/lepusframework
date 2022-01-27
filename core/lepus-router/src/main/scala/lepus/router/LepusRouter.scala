/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router

import lepus.router.http._

trait LepusRouter {
  implicit def stringToPath[T](str: String): RequestEndpoint.FixedPath[String] =
    RequestEndpoint.FixedPath(str, EndpointConverter.string)

  def bindPath[T](implicit converter: EndpointConverter[String, T]): RequestEndpoint.AnyPath[T] =
    RequestEndpoint.AnyPath(None, implicitly)
  def bindPath[T](name: String)(implicit converter: EndpointConverter[String, T]): RequestEndpoint.AnyPath[T] =
    RequestEndpoint.AnyPath(Some(name), implicitly)
}
