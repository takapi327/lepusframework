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

  /** A method to convert the Filed value held by Schema into a comma-separated string and replace it with Fragment. If
    * Schema is not an Entity, it will be *.
    *
    * @param schema
    *   Schema that defines the parameters of the specified model.
    * @tparam T
    *   Arbitrary types, mostly classes are expected
    */
  def schemaToFragment[T](schema: Schema[T]): Fragment =
    schema.schemaType match
      case v: Entity[T] => Fragment.const(v.fields.map(_.name.name).mkString(", "))
      case _            => Fragment.const("*")

  /** A method to convert the Filed value that Schema has to any naming convention, then convert it to a comma-separated
    * string and replace it with Fragment. If Schema is not an Entity, it will be *.
    *
    * @param schema
    *   Schema that defines the parameters of the specified model.
    * @param naming
    *   Arbitrary naming convention value
    * @tparam T
    *   Arbitrary types, mostly classes are expected
    */
  def schemaToFragment[T](schema: Schema[T], naming: Naming): Fragment =
    schema.schemaType match
      case v: Entity[T] => Fragment.const(v.fields.map(s => naming.format(s.name.name)).mkString(", "))
      case _            => Fragment.const("*")

  /** A method to convert the Filed value that Schame has into a comma-delimited string.
    *
    * @param schema
    *   Schema that defines the parameters of the specified model.
    * @tparam T
    *   Arbitrary types, mostly classes are expected
    */
  def schemaFieldNames[T](schema: Schema[T]): String =
    schema.schemaType match
      case v: Entity[T] => v.fields.map(_.name.name).mkString(", ")
      case _            => ""

  /** A method for converting the Filed value that Schema has to any naming convention and converting it to a
    * comma-delimited string.
    *
    * @param schema
    *   Schema that defines the parameters of the specified model.
    * @param naming
    *   Arbitrary naming convention value
    * @tparam T
    *   Arbitrary types, mostly classes are expected
    */
  def schemaFieldNames[T](schema: Schema[T], naming: Naming): String =
    schema.schemaType match
      case v: Entity[T] => v.fields.map(s => naming.format(s.name.name)).mkString(", ")
      case _            => ""

  /** Methods for extracting the number of Field values in Schema
    *
    * @param schema
    *   Schema that defines the parameters of the specified model.
    * @tparam T
    *   Arbitrary types, mostly classes are expected
    */
  def schemaFieldSize[T](schema: Schema[T]): Int =
    schema.schemaType match
      case v: Entity[T] => v.fields.size
      case _            => 0

  /** A specified number of ? method to generate a comma-delimited string.
    *
    * @param size
    *   ? The number you want to generate
    */
  def buildAnyValues(size: Int): String =
    Vector.fill(size)("?").mkString(", ")
