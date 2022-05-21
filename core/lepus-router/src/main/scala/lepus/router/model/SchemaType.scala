/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.model

sealed trait SchemaType[T] {
  def thisType: String
}

object SchemaType {

  case class SString[T]() extends SchemaType[T] {
    override def thisType: String = "string"
  }

  case class SInteger[T]() extends SchemaType[T] {
    override def thisType: String = "integer"
  }

  case class SNumber[T]() extends SchemaType[T] {
    override def thisType: String = "number"
  }

  case class SBoolean[T]() extends SchemaType[T] {
    override def thisType: String = "boolean"
  }

  case class SOption[T, S](element: Schema[S]) extends SchemaType[T] {
    override def thisType: String = s"option(${ element.thisType })"
  }

  case class SArray[T, S](element: Schema[S]) extends SchemaType[T] {
    override def thisType: String = s"array(${ element.thisType })"
  }

  case class SBinary[T]() extends SchemaType[T] {
    override def thisType: String = "binary"
  }

  case class SDate[T]() extends SchemaType[T] {
    override def thisType: String = "date"
  }

  case class SDateTime[T]() extends SchemaType[T] {
    override def thisType: String = "date-time"
  }

  case class Entity[T](fields: List[Entity.Field[T]]) extends SchemaType[T] {
    def required: List[Entity.Field.Name] = fields.collect { case f if !f.schema.isOptional => f.name }
    override def thisType: String =
      s"object(${ fields.map(field => s"${ field.name }->${ field.schema.thisType }").mkString(",") })"
  }

  case class Trait[T](subtypes: List[Schema[_]])(subtypeSchema: T => Option[SchemaWithValue[_]])
    extends SchemaType[T] {
    override def thisType: String = "oneOf:" + subtypes.map(_.thisType).mkString(",")
  }

  case class SchemaWithValue[T](schema: Schema[T], value: T)

  object Entity {
    trait Field[T] {
      type FiledType
      def name:   Field.Name
      def schema: Schema[FiledType]
      def thisType: String = s"field($name, ${ schema.thisType })"
    }

    object Field {
      case class Name(name: String, encodedName: String)

      def apply[T, S](_name: Field.Name, _schema: Schema[S]): Field[T] =
        new Field[T] {
          override type FiledType = S
          override val name:   Name       = _name
          override val schema: Schema[S] = _schema.asInstanceOf[Schema[FiledType]]
        }
    }
  }

}
