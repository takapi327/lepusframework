/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.database

import cats.implicits.*

import doobie.util.fragment.Fragment

trait LepusQuery:
  def fragment: Fragment

object LepusQuery:

  def select(table: String, params: String*): Select =
    Select(fr"SELECT" ++ Fragment.const(params.mkString(",")) ++ fr"FROM" ++ Fragment.const(table))

  case class Select(fragment: Fragment) extends LepusQuery:
    def where(other: Fragment): Where =
      Where(fragment ++ fr"WHERE" ++ other)

  case class Where(fragment: Fragment) extends LepusQuery:
    def and(other: Fragment): Where =
      this.copy(fragment ++ fr"AND" ++ other)
    def or(other: Fragment): Where =
      this.copy(fragment ++ fr"OR" ++ other)
