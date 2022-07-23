/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router.model

sealed trait SchemaType[T](`type`: String):
  type ThisType = T
  override def toString = `type`

object SchemaType:
  case class SString[T]()   extends SchemaType[T]("string")
  case class SInteger[T]()  extends SchemaType[T]("integer")
  case class SNumber[T]()   extends SchemaType[T]("number")
  case class SBoolean[T]()  extends SchemaType[T]("boolean")
  case class SBinary[T]()   extends SchemaType[T]("binary")
  case class SDate[T]()     extends SchemaType[T]("date")
  case class SDateTime[T]() extends SchemaType[T]("date-time")

  case class SOption[T, S](element: Schema[S]) extends SchemaType[T](s"option(${ element.thisType })")

  case class SArray[T, S](element: Schema[S]) extends SchemaType[T](s"array(${ element.thisType })")

  case class Entity[T](fields: List[Entity.Field]) extends SchemaType[T](
    s"object(${ fields.map(field => s"${ field.name }->${ field.schema.thisType }").mkString(",") })"
  ) {
    def required: List[Entity.Field.Name] = fields.collect { case f if !f.schema.isOptional => f.name }
  }

  case class Trait[T](subtypes: List[Schema[_]])(subtypeSchema: T => Option[SchemaWithValue[_]]) extends SchemaType[T](
    "oneOf:" + subtypes.map(_.thisType).mkString(",")
  )

  case class SchemaWithValue[T](schema: Schema[T], value: T)

  object Entity:
    trait Field {
      type FiledType
      def name:   Field.Name
      def schema: Schema[FiledType]
      def thisType: String = s"field($name, ${ schema.thisType })"
    }

    object Field {
      case class Name(name: String, encodedName: String)

      def apply[S](_name: Field.Name, _schema: Schema[S]): Field =
        new Field {
          override type FiledType = S
          override val name:   Name      = _name
          override val schema: Schema[S] = _schema.asInstanceOf[Schema[FiledType]]
        }
    }
