/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import scala.util.matching.Regex

import cats.kernel.Monoid
import cats.syntax.order.*

/** An object for setting conditions to determine whether or not logging should be performed.
  *
  * copied from woof:
  * https://github.com/LEGO/woof/blob/main/modules/core/shared/src/main/scala/org/legogroup/woof/Filter.scala
  */
type Filter = LogMessage => Boolean

object Filter:
  val atLeastLevel: Level => Filter = level => log => log.level >= level
  val exactLevel:   Level => Filter = level => log => log.level == level
  val regexFilter:  Regex => Filter = regex => log => regex.matches(log.execLocation.fileName)
  val nothing:      Filter          = _ => false
  val everything:   Filter          = _ => true

  given Monoid[Filter] with
    def empty:                         Filter = nothing
    def combine(f: Filter, g: Filter): Filter = f or g

  extension (f: Filter)
    infix def and(g: Filter): Filter = line => f(line) && g(line)
    infix def or(g: Filter):  Filter = line => f(line) || g(line)
