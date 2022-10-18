/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import scala.annotation.targetName

import cats.implicits.*

import lepus.core.generic.Schema
import lepus.core.format.Naming

trait LepusQuery[T: Read]:
  def fragment: Fragment

  /** Alias for the method that Fragment has. */
  def query(using log: LogHandler):     Query0[T]         = fragment.queryWithLogHandler[T](log)
  def update(using log: LogHandler):    Update0           = fragment.updateWithLogHandler(log)
  def updateRun(using log: LogHandler): ConnectionIO[Int] = fragment.updateWithLogHandler(log).run

  val sql: String = fragment.internals.sql

  override def toString: String = sql

object LepusQuery extends SchemaHelper:

  def select[T: Read](table: String, params: String*): Select[T] =
    Select(fr"SELECT" ++ Fragment.const(params.mkString(",")) ++ fr"FROM" ++ Fragment.const(table))
  def select[T: Read: Schema](table: String): Select[T] =
    Select(fr"SELECT" ++ schemaToFragment(summon[Schema[T]]) ++ fr"FROM" ++ Fragment.const(table))
  def select[T: Read: Schema](table: String, naming: Naming): Select[T] =
    Select(fr"SELECT" ++ schemaToFragment(summon[Schema[T]], naming) ++ fr"FROM" ++ Fragment.const(table))
  def insert[T: Read](table: String, params: String*): Insert[T] =
    Insert(fr"INSERT INTO" ++ Fragment.const(table) ++ fr"(" ++ Fragment.const(params.mkString(",")) ++ fr")")
  def insert[T: Read: Schema](table: String): Insert[T] =
    Insert(fr"INSERT INTO" ++ Fragment.const(table) ++ fr"(" ++ schemaToFragment(summon[Schema[T]]) ++ fr")")
  def insert[T: Read: Schema](table: String, naming: Naming): Insert[T] =
    Insert(fr"INSERT INTO" ++ Fragment.const(table) ++ fr"(" ++ schemaToFragment(summon[Schema[T]], naming) ++ fr")")
  def update[T: Read](table: String, params: Fragment*): Update[T] = Update(
    fr"UPDATE" ++ Fragment.const(table) ++ fr"SET" ++ params.intercalate(fr",")
  )
  def delete[T: Read](table: String): Delete[T] = Delete(fr"DELETE FROM" ++ Fragment.const(table))

  case class Select[T: Read](fragment: Fragment) extends LepusQuery[T]:
    def where(other: Fragment): Where[T] =
      Where(fragment ++ fr"WHERE" ++ other)
    def limit(num: Long): Limit[T] =
      require(num > 0, "The LIMIT condition must be a number greater than 0.")
      Limit(fragment ++ fr"LIMIT" ++ Fragment.const(num.toString))

  case class Update[T: Read](fragment: Fragment) extends LepusQuery[T]:
    def where(other: Fragment): Where[T] =
      Where(fragment ++ fr"WHERE" ++ other)

  case class Delete[T: Read](fragment: Fragment) extends LepusQuery[T]:
    def where(other: Fragment): Where[T] =
      Where(fragment ++ fr"WHERE" ++ other)

  case class Where[T: Read](fragment: Fragment) extends LepusQuery[T]:
    def and(other: Fragment): Where[T] =
      this.copy(fragment ++ fr"AND" ++ other)
    def or(other: Fragment): Where[T] =
      this.copy(fragment ++ fr"OR" ++ other)
    def limit(num: Long): Limit[T] =
      require(num > 0, "The LIMIT condition must be a number greater than 0.")
      Limit(fragment ++ fr"LIMIT" ++ Fragment.const(num.toString))

  case class Limit[T: Read](fragment: Fragment) extends LepusQuery[T]

  case class Insert[T: Read](fragment: Fragment) extends LepusQuery[T]:
    def values(params: Fragment*): Insert[T] =
      this.copy(fragment ++ fr"VALUES" ++ fr"(" ++ params.intercalate(fr",") ++ fr")")
