/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.core.internal

import scala.collection.immutable.{ Vector, ListMap }

trait ExtensionMethods:
  extension [A](xs: Iterable[A])
    def toListMap[T, U](using ev: A <:< (T, U)): ListMap[T, U] =
      val b = ListMap.newBuilder[T, U]
      for (x <- xs)
        b += x

      b.result()
