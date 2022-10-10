/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import scala.annotation.targetName

import cats.implicits.*

import doobie.util.fragment.Fragment

trait LepusQuery:
  def fragment: Fragment

  /** Alias for the method that Fragment has. */
  def query[B: Read](using h: LogHandler = LogHandler.nop): Query0[B] =
    fragment.query[B]
  def update(using h: LogHandler = LogHandler.nop): Update0 =
    fragment.update

object LepusQuery:

  def select(table: String, params: String*): Select =
    Select(fr"SELECT" ++ Fragment.const(params.mkString(",")) ++ fr"FROM" ++ Fragment.const(table))
  def insert(table: String, params: String*): Insert =
    Insert(fr"INSERT INTO" ++ Fragment.const(table) ++ fr"(" ++ Fragment.const(params.mkString(",")) ++ fr")")

  case class Select(fragment: Fragment) extends LepusQuery:
    def where(other: Fragment): Where =
      Where(fragment ++ fr"WHERE" ++ other)
    def limit(num: Long): Limit =
      require(num > 0, "The LIMIT condition must be a number greater than 0.")
      Limit(fragment ++ fr"LIMIT" ++ Fragment.const(num.toString))

  case class Where(fragment: Fragment) extends LepusQuery:
    def and(other: Fragment): Where =
      this.copy(fragment ++ fr"AND" ++ other)
    def or(other: Fragment): Where =
      this.copy(fragment ++ fr"OR" ++ other)
    def limit(num: Long): Limit =
      require(num > 0, "The LIMIT condition must be a number greater than 0.")
      Limit(fragment ++ fr"LIMIT" ++ Fragment.const(num.toString))

  case class Limit(fragment: Fragment) extends LepusQuery

  case class Insert(fragment: Fragment) extends LepusQuery:
    def values(params: Fragment*): Insert =
      this.copy(fragment ++ fr"values" ++ fr"(" ++ params.intercalate(fr",") ++ fr")")
