/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router.generic

import language.experimental.macros

import magnolia1._

import lepus.router.model.{ Schema, SchemaType }

import SchemaType._
trait SchemaDerivation {

  type Typeclass[T] = Schema[T]

  def join[T](ctx: ReadOnlyCaseClass[Schema, T]): Schema[T] = {
    if (ctx.isValueClass) {
      require(ctx.parameters.nonEmpty, s"Cannot derive schema for generic value class: ${ ctx.typeName.owner }")
      val valueSchema = ctx.parameters.head.typeclass
      Schema[T](
        schemaType = valueSchema.schemaType.asInstanceOf[SchemaType[T]],
        format     = valueSchema.format
      )
    } else {
      Schema[T](
        schemaType = entitySchemaType(ctx),
        name       = Some(typeNameToSchemaName(ctx.typeName))
      )
    }
  }

  def split[T](ctx: SealedTrait[Schema, T]): Schema[T] = {
    val subtypesByName = ctx.subtypes
      .map(subtype => {
        typeNameToSchemaName(subtype.typeName) -> subtype.typeclass.asInstanceOf[Typeclass[T]]
      })
      .toListMap
    val traitType = Trait(subtypesByName.values.toList)((t: T) =>
      ctx.split(t) { v =>
        for {
          schema <- subtypesByName.get(typeNameToSchemaName(v.typeName))
          value  <- v.cast.lift(t)
        } yield SchemaWithValue(schema, value)
      }
    )
    Schema[T](
      schemaType = traitType,
      name       = Some(typeNameToSchemaName(ctx.typeName))
    )
  }

  private def typeNameToSchemaName(typeName: TypeName): Schema.Name =
    Schema.Name(
      fullName       = typeName.full,
      typeParameters = allTypeArguments(typeName).map(_.short).toList
    )

  private def entitySchemaType[T](ctx: ReadOnlyCaseClass[Schema, T]): Entity[T] =
    Entity(
      ctx.parameters.map { param =>
        Entity.Field[T, param.PType](
          _name   = Entity.Field.Name(param.label, param.label),
          _schema = param.typeclass
        )
      }.toList
    )

  private def allTypeArguments(typeName: TypeName): Seq[TypeName] =
    typeName.typeArguments.flatMap(v => v +: allTypeArguments(v))

  implicit def schemaGen[T]: Schema[T] = macro Magnolia.gen[T]
}
