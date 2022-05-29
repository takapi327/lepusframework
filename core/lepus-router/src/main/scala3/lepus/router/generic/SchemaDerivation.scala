/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router.generic

import scala.deriving.Mirror

import magnolia1.*

import lepus.router.*
import model.{ Schema, SchemaType }
import SchemaType.*

trait SchemaDerivation:

  inline def derived[T](using Mirror.Of[T]): Schema[T] =
    val derivation = new Derivation[Schema]:
      type Typeclass[T] = Schema[T]

      override def join[T](ctx: CaseClass[Schema, T]): Schema[T] =
        if (ctx.isValueClass)
          require(ctx.params.nonEmpty, s"Cannot derive schema for generic value class: ${ ctx.typeInfo.owner }")
          val valueSchema = ctx.params.head.typeclass
          Schema[T](
            schemaType = valueSchema.schemaType.asInstanceOf[SchemaType[T]],
            format     = valueSchema.format
          )
        else
          Schema[T](
            schemaType = entitySchemaType(ctx),
            name       = Some(typeNameToSchemaName(ctx.typeInfo))
          )

      override def split[T](ctx: SealedTrait[Schema, T]): Schema[T] =
        val subtypesByName = ctx.subtypes
          .toList
          .map(subtype =>
            typeNameToSchemaName(subtype.typeInfo) -> subtype.typeclass.asInstanceOf[Typeclass[T]]
          )
          .toListMap
        val traitType = Trait(subtypesByName.values.toList)((t: T) =>
          ctx.choose(t) { v =>
            for
              schema <- subtypesByName.get(typeNameToSchemaName(v.typeInfo))
              value  <- v.cast.lift(t)
            yield SchemaWithValue(schema, value)
          }
        )
        Schema[T](
          schemaType = traitType,
          name       = Some(typeNameToSchemaName(ctx.typeInfo))
        )

      private def typeNameToSchemaName(typeInfo: TypeInfo): Schema.Name =
        Schema.Name(
          fullName       = typeInfo.full,
          typeParameters = allTypeArguments(typeInfo).map(_.short).toList
        )

      private def entitySchemaType[T](ctx: CaseClass[Schema, T]): Entity[T] =
        Entity(
          ctx.params.map { param =>
            Entity.Field[T, param.PType](
              _name   = Entity.Field.Name(param.label, param.label),
              _schema = param.typeclass
            )
          }.toList
        )

      private def allTypeArguments(typeInfo: TypeInfo): Seq[TypeInfo] =
        typeInfo.typeParams.toList.flatMap(v => v +: allTypeArguments(v))

    derivation.derived[T]

end SchemaDerivation
