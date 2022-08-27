/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.internal

import lepus.router.http.*

trait ExtensionMethods extends lepus.core.internal.ExtensionMethods:

  extension (endpoint: Endpoint[?])
    def recursiveEndpoints[T](pf: PartialFunction[Endpoint[?], Vector[T]]): Vector[T] =
      endpoint match
        case Endpoint.Pair(left, right)          => left.recursiveEndpoints(pf) ++ right.recursiveEndpoints(pf)
        case r: Endpoint[?] if pf.isDefinedAt(r) => pf(r)
        case _                                   => Vector.empty

    def asVector(): Vector[Endpoint[?]] =
      recursiveEndpoints {
        case e: Endpoint[?] => Vector(e)
      }

    def toPath: String = "/" + asVector()
      .map {
        case e: Endpoint.FixedPath[?] => e.name
        case e: Endpoint.Path[?]      => s"{${ e.name }}"
        case _                        => ""
      }
      .mkString("/")

    def isPath: Boolean =
      endpoint match
        case _: Endpoint.Path[?] => true
        case _                   => false

    def isQueryParam: Boolean =
      endpoint match
        case _: Endpoint.Query[?] => true
        case _                    => false

  // see https://github.com/scala/bug/issues/12186
  extension [T](v: Vector[T])
    def headAndTail: Option[(T, Vector[T])] = if v.isEmpty then None else Some((v.head, v.tail))
    def initAndLast: Option[(Vector[T], T)] = if v.isEmpty then None else Some((v.init, v.last))
    def toTuple: Any =
      if v.size > 22 then throw new IllegalArgumentException(s"Cannot convert $v to params!")
      else if (v.size == 1) v.head.asInstanceOf[Any]
      else
        val clazz = Class.forName("scala.Tuple" + v.size)
        clazz.getConstructors()(0).newInstance(v.map(_.asInstanceOf[AnyRef]): _*).asInstanceOf[Any]
