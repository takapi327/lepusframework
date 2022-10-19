/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import scala.collection.immutable.ListMap

import cats.data.NonEmptyList

import org.http4s.Method

import doobie.Transactor

import lepus.core.generic.Schema

import lepus.database.DataSource

import lepus.router.*
import lepus.router.internal.*
import lepus.router.http.Endpoint

import lepus.server.LepusApp

import lepus.swagger.model.*

private[lepus] object RouterToOpenAPI:

  val schemaToTuple: SchemaToTuple = SchemaToTuple()

  def generateOpenAPIDocs[F[_]](
    info:   Info,
    router: LepusApp[F]
  ): OpenApiUI =
    given Map[DataSource, Transactor[F]] = Map.empty
    val groupEndpoint                    = router.routes.toList.toMap

    val schemaTuple = routerToSchemaTuple(groupEndpoint)

    val schemaToReference     = SchemaToReference(schemaTuple)
    val schemaToOpenApiSchema = SchemaToOpenApiSchema(schemaToReference)

    val component = schemaTuple.map(v => Component(v.map(x => x._1.shortName -> schemaToOpenApiSchema(x._2))))
    val endpoints = groupEndpoint.map {
      case (endpoint, route) => endpoint.toPath -> routerToPath(endpoint, route, schemaToOpenApiSchema)
    }
    val tags = router.routes.toList
      .flatMap(_._2 match
        case constructor: OpenApiConstructor[F, ?] => constructor.tags
        case _                                     => Set.empty
      )
      .toSet
    OpenApiUI.build(info, endpoints, tags, component)

  private def routerToSchemaTuple[F[_]](
    groupEndpoint: Map[Endpoint[?], OpenApiConstructor[F, ?] | RouterConstructor[F, ?]]
  ): Option[ListMap[Schema.Name, Schema[?]]] =
    val encoded = for
      (_, router) <- groupEndpoint.toList
      method      <- Method.all
    yield router match
      case constructor: OpenApiConstructor[F, ?] =>
        constructor.responses
          .lift(method)
          .map(_.flatMap(res => schemaToTuple(res.schema)).toListMap)
      case _ => None

    encoded.flatten.foldLeft[Option[ListMap[Schema.Name, Schema[?]]]](Some(ListMap.empty)) { (o, ol) =>
      PartialFunction.condOpt(o, ol) {
        case (Some(x), xs) => x ++ xs
      }
    }

  private def routerToPath[F[_]](
    endpoint: Endpoint[?],
    route:    OpenApiConstructor[F, ?] | RouterConstructor[F, ?],
    schema:   SchemaToOpenApiSchema
  ): Map[String, Path] =
    route match
      case constructor: OpenApiConstructor[F, ?] =>
        val methods = Method.all.filter(constructor.responses.isDefinedAt)
        methods
          .map(method => {
            method.toString.toLowerCase -> Path.fromEndpoint(method, endpoint, constructor, schema)
          })
          .toMap
      case _ => Map.empty
