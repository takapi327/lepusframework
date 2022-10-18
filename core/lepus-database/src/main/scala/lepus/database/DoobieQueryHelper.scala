/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import lepus.core.generic.Schema
import lepus.core.format.Naming

trait DoobieQueryHelper extends SchemaHelper:

  /** Name of the database table to use with this helper */
  def table: String

  /** Convenience methods for SELECT statements to connect to databases. */
  def select[T: Read](params: String*): LepusQuery.Select[T] = LepusQuery.select[T](table, params*)
  def select[T: Read: Schema]:          LepusQuery.Select[T] = LepusQuery.select[T](table, SNAKE)

  /** Convenience method for INSERT INTO statement to connect to a database. */
  def insert[T: Read](params: String*):     LepusQuery.Insert[T] = LepusQuery.insert[T](table, params*)
  def insert[T: Read: Schema]:              LepusQuery.Insert[T] = LepusQuery.insert[T](table, SNAKE)
  def insert[T: Write: Schema](value: T):   ConnectionIO[Int]    = update[T](SNAKE).run(value)
  def insert[T: Write: Schema](values: T*): ConnectionIO[Int]    = update[T](SNAKE).updateMany(values)

  /** Convenience method for UPDATE statement to connect to a database. */
  def update[T: Read](params: Fragment*): LepusQuery.Update[T] = LepusQuery.update[T](table, params*)

  /** Convenience method for DELETE statement to connect to a database. */
  def delete[T: Read]: LepusQuery.Delete[T] = LepusQuery.delete[T](table)

  private def update[T: Write](using schema: Schema[T]): Update[T] =
    Update[T](
      s"INSERT INTO $table (${ schemaFieldNames(schema) }) VALUES (${ buildAnyValues(schemaFieldSize(schema)) })"
    )

  private def update[T: Write](naming: Naming)(using schema: Schema[T]): Update[T] =
    Update[T](
      s"INSERT INTO $table (${ schemaFieldNames(schema, naming) }) VALUES (${ buildAnyValues(schemaFieldSize(schema)) })"
    )
