/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import lepus.core.generic.Schema
import lepus.core.generic.SchemaType.Entity

trait DoobieQueryHelper:
  log: DoobieLogHandler =>

  def table: String

  def select(params: String*): LepusQuery.Select = LepusQuery.select(table, params*)
  def select[T: Schema]:       LepusQuery.Select = LepusQuery.select[T](table)
  def insert(params: String*): LepusQuery.Insert = LepusQuery.insert(table, params*)
  def insert[T: Schema]:       LepusQuery.Insert = LepusQuery.insert[T](table)

  def insert[T: Write: Schema](value: T): ConnectionIO[Int] =
    given LogHandler = log.logHandler
    Update[T](
      s"INSERT INTO $table (${ schemaFieldNames(summon[Schema[T]]) }) VALUES (${ buildAnyValues(schemaFieldSize(summon[Schema[T]])) })"
    ).run(value)

  private[lepus] def schemaFieldNames[T](schema: Schema[T]): String =
    schema.schemaType match
      case v: Entity[T] => v.fields.map(_.name.name).mkString(", ")
      case _            => ""

  private[lepus] def schemaFieldSize[T](schema: Schema[T]): Int =
    schema.schemaType match
      case v: Entity[T] => v.fields.size
      case _            => 0

  private def buildAnyValues(size: Int): String =
    Vector.fill(size)("?").mkString(",")
