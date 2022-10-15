/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import scala.annotation.targetName

import cats.implicits.*

import lepus.core.generic.Schema
import lepus.core.format.Naming

trait LepusQuery:
  def fragment: Fragment

  /** Alias for the method that Fragment has. */
  def query[B: Read](using h: LogHandler = LogHandler.nop): Query0[B] =
    fragment.query[B]
  def update(using h: LogHandler = LogHandler.nop): Update0 =
    fragment.update
  def updateRun(using h: LogHandler = LogHandler.nop): ConnectionIO[Int] =
    fragment.update.run

object LepusQuery extends SchemaHelper:

  def select(table: String, params: String*): Select =
    Select(fr"SELECT" ++ Fragment.const(params.mkString(",")) ++ fr"FROM" ++ Fragment.const(table))
  def select[T: Schema](table: String): Select =
    Select(fr"SELECT" ++ schemaToFragment(summon[Schema[T]]) ++ fr"FROM" ++ Fragment.const(table))
  def select[T: Schema](table: String, naming: Naming): Select =
    Select(fr"SELECT" ++ schemaToFragment(summon[Schema[T]], naming) ++ fr"FROM" ++ Fragment.const(table))
  def insert(table: String, params: String*): Insert =
    Insert(fr"INSERT INTO" ++ Fragment.const(table) ++ fr"(" ++ Fragment.const(params.mkString(",")) ++ fr")")
  def insert[T: Schema](table: String): Insert =
    Insert(fr"INSERT INTO" ++ Fragment.const(table) ++ fr"(" ++ schemaToFragment(summon[Schema[T]]) ++ fr")")
  def insert[T: Schema](table: String, naming: Naming): Insert =
    Insert(fr"INSERT INTO" ++ Fragment.const(table) ++ fr"(" ++ schemaToFragment(summon[Schema[T]], naming) ++ fr")")
  def delete(table: String): Delete =
    Delete(fr"DELETE" ++ Fragment.const(table))

  case class Select(fragment: Fragment) extends LepusQuery:
    def where(other: Fragment): Where =
      Where(fragment ++ fr"WHERE" ++ other)
    def limit(num: Long): Limit =
      require(num > 0, "The LIMIT condition must be a number greater than 0.")
      Limit(fragment ++ fr"LIMIT" ++ Fragment.const(num.toString))

  case class Delete(fragment: Fragment) extends LepusQuery:
    def where(other: Fragment): Where =
      Where(fragment ++ fr"WHERE" ++ other)

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
      this.copy(fragment ++ fr"VALUES" ++ fr"(" ++ params.intercalate(fr",") ++ fr")")
