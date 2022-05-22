/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import scala.collection.immutable.ListMap

import cats.data.NonEmptyList

import lepus.router.{ RouterConstructor, RouterProvider }
import lepus.swagger.model._

object RouterToOpenAPI {

  val schemaToTuple         = new SchemaToTuple()
  val schemaToOpenApiSchema = new SchemaToOpenApiSchema()

  def generateOpenAPIDocs[F[_]](
    info:   Info,
    router: RouterProvider[F]
  ): OpenApiUI = {
    val groupEndpoint   = router.routes.groupBy(_.endpoint.toPath)
    val component       = routerToComponent(groupEndpoint).map(Component)
    val endpoints       = groupEndpoint.map(v => v._1 -> routerToPath(v._2))
    OpenApiUI.build(info, endpoints, router.tags, component)
  }

  private def routerToComponent[F[_]](groupEndpoint: Map[String, NonEmptyList[RouterConstructor[F]]]): Option[ListMap[String, Either[Reference, OpenApiSchema]]] = {
    (for {
      (_, routes) <- groupEndpoint.toList
      router      <- routes.toList
      method      <- router.methods
    } yield {
      router.responses
        .lift(method)
        .map(_.flatMap(res => schemaToTuple(res.schema))
          .map(v => v._1.shortName -> schemaToOpenApiSchema(v._2))
          .toListMap
        )
    }).flatten.foldLeft[Option[ListMap[String, Either[Reference, OpenApiSchema]]]](Some(ListMap.empty)) { (o, ol) =>
      PartialFunction.condOpt(o, ol) {
        case (Some(x), xs) => x ++ xs
      }
    }
  }

  private def routerToPath[F[_]](routes: NonEmptyList[RouterConstructor[F]]): Map[String, Path] =
    (for {
      router <- routes.toList
      method <- router.methods
    } yield {
      method.toString().toLowerCase -> Path.fromEndpoint(method, router, schemaToOpenApiSchema)
    }).toMap
}
