/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router.generic

import language.experimental.macros

import magnolia1._

import lepus.router.model.{ SchemaL, SchemaType }

import SchemaType._
trait SchemaDerivation {

  type Typeclass[T] = SchemaL[T]

  def join[T](ctx: ReadOnlyCaseClass[SchemaL, T]): SchemaL[T] = {
    if (ctx.isValueClass) {
      require(ctx.parameters.nonEmpty, s"Cannot derive schema for generic value class: ${ctx.typeName.owner}")
      val valueSchema = ctx.parameters.head.typeclass
      SchemaL[T](
        schemaType = valueSchema.schemaType.asInstanceOf[SchemaType[T]],
        format     = valueSchema.format
      )
    } else {
      SchemaL[T](
        schemaType = entitySchemaType(ctx),
        name       = Some(typeNameToSchemaName(ctx.typeName))
      )
    }
  }

  def split[T](ctx: SealedTrait[SchemaL, T]): SchemaL[T] = {
    val subtypesByName = ctx.subtypes.map(subtype => {
      typeNameToSchemaName(subtype.typeName) -> subtype.typeclass.asInstanceOf[Typeclass[T]]
    }).toListMap
    val traitType = Trait(subtypesByName.values.toList)((t: T) =>
      ctx.split(t) { v =>
        for {
          schema <- subtypesByName.get(typeNameToSchemaName(v.typeName))
          value  <- v.cast.lift(t)
        } yield SchemaWithValue(schema, value)
      }
    )
    SchemaL[T](
      schemaType = traitType,
      name       = Some(typeNameToSchemaName(ctx.typeName))
    )
  }

  implicit def gen[T]: SchemaL[T] = macro Magnolia.gen[T]

  private def typeNameToSchemaName(typeName: TypeName): SchemaL.Name =
    SchemaL.Name(
      fullName       = typeName.full,
      typeParameters = allTypeArguments(typeName).map(_.short).toList
    )

  private def entitySchemaType[T](ctx: ReadOnlyCaseClass[SchemaL, T]): Entity[T] =
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
}
