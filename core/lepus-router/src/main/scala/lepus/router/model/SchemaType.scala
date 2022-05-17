/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router.model

sealed trait SchemaType[T] {
  def thisType: String
}

object SchemaType {

  case class String[T]() extends SchemaType[T] {
    override def thisType: Predef.String = "string"
  }

  case class Integer[T]() extends SchemaType[T] {
    override def thisType: Predef.String = "integer"
  }

  case class Number[T]() extends SchemaType[T] {
    override def thisType: Predef.String = "number"
  }

  case class Boolean[T]() extends SchemaType[T] {
    override def thisType: Predef.String = "boolean"
  }

  case class Option[T, S](element: SchemaL[S]) extends SchemaType[T] {
    override def thisType: Predef.String = s"option(${element.thisType})"
  }

  case class Array[T, S](element: SchemaL[S]) extends SchemaType[T] {
    override def thisType: Predef.String = s"array(${element.thisType})"
  }

  case class Binary[T]() extends SchemaType[T] {
    override def thisType: Predef.String = "binary"
  }

  case class Date[T]() extends SchemaType[T] {
    override def thisType: Predef.String = "date"
  }

  case class DateTime[T]() extends SchemaType[T] {
    override def thisType: Predef.String = "date-time"
  }
}
