/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import scala.collection.immutable.ListMap

import cats.data.NonEmptyList

import lepus.router.*
import model.Schema
import lepus.router.internal.*
import lepus.router.{ RouterConstructor, RouterProvider }

import lepus.swagger.model.*

private[lepus] object RouterToOpenAPI:

  val schemaToTuple: SchemaToTuple = SchemaToTuple()

  def generateOpenAPIDocs[F[_]](
    info:   Info,
    router: RouterProvider[F] & OpenApiProvider[F]
  ): OpenApiUI =
    val groupEndpoint = router.routes.groupBy(_.endpoint.toPath)

    val schemaTuple = routerToSchemaTuple(groupEndpoint)

    val schemaToReference     = SchemaToReference(schemaTuple)
    val schemaToOpenApiSchema = SchemaToOpenApiSchema(schemaToReference)

    val component = schemaTuple.map(v => Component(v.map(x => x._1.shortName -> schemaToOpenApiSchema(x._2))))
    val endpoints = groupEndpoint.map(v => v._1 -> routerToPath(v._2, schemaToOpenApiSchema))
    OpenApiUI.build(info, endpoints, router.routes.toList.flatMap(_.tags).toSet, component)

  private def routerToSchemaTuple[F[_]](
    groupEndpoint: Map[String, NonEmptyList[RouterConstructor[F, ?] & OpenApiConstructor[F, ?]]]
  ): Option[ListMap[Schema.Name, Schema[?]]] =
    val encoded = for
      (_, routes) <- groupEndpoint.toList
      router      <- routes.toList
      method      <- router.methods
    yield router.responses
      .lift(method)
      .map(_.flatMap(res => schemaToTuple(res.schema)).toListMap)

    encoded.flatten.foldLeft[Option[ListMap[Schema.Name, Schema[?]]]](Some(ListMap.empty)) { (o, ol) =>
      PartialFunction.condOpt(o, ol) {
        case (Some(x), xs) => x ++ xs
      }
    }

  private def routerToPath[F[_]](
    routes:                NonEmptyList[RouterConstructor[F, ?] & OpenApiConstructor[F, ?]],
    schemaToOpenApiSchema: SchemaToOpenApiSchema
  ): Map[String, Path] =
    (for
      router <- routes.toList
      method <- router.methods
    yield method.toString.toLowerCase -> Path.fromEndpoint(method, router, schemaToOpenApiSchema)).toMap
