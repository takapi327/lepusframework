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
import lepus.router.http.RequestEndpoint
import lepus.swagger.model.*

object RouterToOpenAPI:

  val schemaToTuple: SchemaToTuple = SchemaToTuple()

  def generateOpenAPIDocs[F[_]](
    info:   Info,
    router: RouterProvider[F]
  ): OpenApiUI =
    val groupEndpoint = router.routes.toList.toMap

    val schemaTuple = routerToSchemaTuple(groupEndpoint)

    val schemaToReference     = SchemaToReference(schemaTuple)
    val schemaToOpenApiSchema = SchemaToOpenApiSchema(schemaToReference)

    val component = schemaTuple.map(v => Component(v.map(x => x._1.shortName -> schemaToOpenApiSchema(x._2))))
    val endpoints = groupEndpoint.map {
      case (endpoint, route) => endpoint.toPath -> routerToPath(endpoint -> route, schemaToOpenApiSchema)
    }
    OpenApiUI.build(info, endpoints, router.tags, component)

  private def routerToSchemaTuple[F[_]](
    groupEndpoint: Map[RequestEndpoint.Endpoint, RouterConstructor[F, ?]]
  ): Option[ListMap[Schema.Name, Schema[?]]] =
    val encoded = for
      (_, router) <- groupEndpoint.toList
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
    router: Route[F],
    schema: SchemaToOpenApiSchema
  ): Map[String, Path] =
    val (endpoint, route) = router
    (for method <- route.methods
    yield method.toString.toLowerCase -> Path.fromEndpoint(method, endpoint, route, schema)).toMap
