/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import cats.Show
import cats.kernel.{ Order, LowerBounded, PartialOrder, UpperBounded }

/** Log level
  */
enum Level:
  case Trace, Debug, Info, Warn, Error

extension (level: Level)
  def toString: String      = level.toString.toUpperCase
  def show:     Show[Level] = Show.fromToString[Level]

/** It is used to define the full order for [[Level]]
  */
given Order[Level] with LowerBounded[Level] with UpperBounded[Level] with {
  self =>

  def compare(x: Level, y: Level): Int = x.ordinal compare y.ordinal

  def minBound: Level = Level.Trace

  def maxBound: Level = Level.Error

  def partialOrder: PartialOrder[Level] = self
}
