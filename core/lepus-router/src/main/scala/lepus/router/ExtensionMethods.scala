/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router

import scala.collection.immutable.Vector

import lepus.router.http._

trait ExtensionMethods {

  implicit class RequestEndpointOps[T](endpoint: RequestEndpoint[T]) {
    def recursiveEndpoints[T](pf: PartialFunction[RequestEndpoint[_], Vector[T]]): Vector[T] = {
      endpoint match {
        case RequestEndpoint.Pair(left, right)          => left.recursiveEndpoints(pf) ++ right.recursiveEndpoints(pf)
        case r: RequestEndpoint[_] if pf.isDefinedAt(r) => pf(r)
        case _                                          => Vector.empty
      }
    }

    def asVector(): Vector[RequestEndpoint[_]] = {
      recursiveEndpoints {
        case e: RequestEndpoint[_] => Vector(e)
      }
    }
  }
}
