/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import lepus.core.generic.Schema
import lepus.core.generic.SchemaType.Entity
import lepus.core.format.Naming

trait SchemaHelper:
  /** Naming Rule Aliases */
  protected final val CAMEL  = Naming.CAMEL
  protected final val PASCAL = Naming.PASCAL
  protected final val SNAKE  = Naming.SNAKE
  protected final val KEBAB  = Naming.KEBAB

  def schemaToFragment[T](schema: Schema[T]): Fragment =
    schema.schemaType match
      case v: Entity[T] => Fragment.const(v.fields.map(_.name.name).mkString(", "))
      case _            => Fragment.const("*")

  def schemaToFragment[T](schema: Schema[T], naming: Naming): Fragment =
    schema.schemaType match
      case v: Entity[T] => Fragment.const(v.fields.map(s => naming.format(s.name.name)).mkString(", "))
      case _            => Fragment.const("*")

  def schemaFieldNames[T](schema: Schema[T]): String =
    schema.schemaType match
      case v: Entity[T] => v.fields.map(_.name.name).mkString(", ")
      case _            => ""

  def schemaFieldNames[T](schema: Schema[T], naming: Naming): String =
    schema.schemaType match
      case v: Entity[T] => v.fields.map(s => naming.format(s.name.name)).mkString(", ")
      case _            => ""

  def schemaFieldSize[T](schema: Schema[T]): Int =
    schema.schemaType match
      case v: Entity[T] => v.fields.size
      case _            => 0

  def buildAnyValues(size: Int): String =
    Vector.fill(size)("?").mkString(", ")
