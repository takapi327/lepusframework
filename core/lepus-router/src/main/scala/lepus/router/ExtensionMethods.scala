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

    def isPath(): Boolean =
      endpoint match {
        case _: RequestEndpoint.FixedPath[_]  => true
        case _: RequestEndpoint.PathParam[_]  => true
        case _: RequestEndpoint.QueryParam[_] => false
        case _                                => false
      }

    def isQueryParam(): Boolean =
      endpoint match {
        case _: RequestEndpoint.FixedPath[_]  => false
        case _: RequestEndpoint.PathParam[_]  => false
        case _: RequestEndpoint.QueryParam[_] => true
        case _                                => false
      }
  }

  // see https://github.com/scala/bug/issues/12186
  implicit class VectorOps[T](v: Vector[T]) {
    def headAndTail: Option[(T, Vector[T])] = if (v.isEmpty) None else Some((v.head, v.tail))
    def initAndLast: Option[(Vector[T], T)] = if (v.isEmpty) None else Some((v.init, v.last))
    def toTuple: Any = {
      if (v.size > 22) {
        throw new IllegalArgumentException(s"Cannot convert $v to params!")
      } else if (v.size == 1) {
        v.head.asInstanceOf[Any]
      } else  {
        val clazz = Class.forName("scala.Tuple" + v.size)
        clazz.getConstructors()(0).newInstance(v.map(_.asInstanceOf[AnyRef]): _*).asInstanceOf[Any]
      }
    }
  }
}
