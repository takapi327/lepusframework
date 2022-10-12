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
  def select(params: String*):           LepusQuery.Select = LepusQuery.select(table, params*)
  def select[T: Schema]:                 LepusQuery.Select = LepusQuery.select[T](table)
  def select[T: Schema](naming: Naming): LepusQuery.Select = LepusQuery.select[T](table, naming)

  /** Convenience method for INSERT INTO statement to connect to a database. */
  def insert(params: String*):           LepusQuery.Insert = LepusQuery.insert(table, params*)
  def insert[T: Schema]:                 LepusQuery.Insert = LepusQuery.insert[T](table)
  def insert[T: Schema](naming: Naming): LepusQuery.Insert = LepusQuery.insert[T](table, naming)
  def insert[T: Write: Schema](value: T): ConnectionIO[Int] =
    update[T].run(value)
  def insert[T: Write: Schema](values: T*): ConnectionIO[Int] =
    update[T].updateMany(values)
  def insert[T: Write: Schema](value: T, naming: Naming): ConnectionIO[Int] =
    update[T](naming).run(value)
  def insert[T: Write: Schema](naming: Naming)(values: T*): ConnectionIO[Int] =
    update[T](naming).updateMany(values)

  private def update[T: Write](using schema: Schema[T]): Update[T] =
    Update[T](
      s"INSERT INTO $table (${ schemaFieldNames(schema) }) VALUES (${ buildAnyValues(schemaFieldSize(schema)) })"
    )

  private def update[T: Write](naming: Naming)(using schema: Schema[T]): Update[T] =
    Update[T](
      s"INSERT INTO $table (${ schemaFieldNames(schema, naming) }) VALUES (${ buildAnyValues(schemaFieldSize(schema)) })"
    )
