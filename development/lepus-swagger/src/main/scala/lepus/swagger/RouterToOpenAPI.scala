/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.swagger

import cats.data.NonEmptyList

import lepus.router.{ RouterConstructor, RouterProvider }

import lepus.swagger.model.Path

object RouterToOpenAPI {

  val schemaToOpenApiSchema = new SchemaToOpenApiSchema()

  def generateOpenAPIDocs[F[_]](
    info:   Info,
    router: RouterProvider[F]
  ): SwaggerUI = {
    val groupEndpoint = router.routes.groupBy(_.endpoint.toPath)
    val endpoints     = groupEndpoint.map(v => v._1 -> routerToPath(v._2))
    SwaggerUI.build(info, endpoints, router.tags)
  }

  private def routerToPath[F[_]](routes: NonEmptyList[RouterConstructor[F]]): Map[String, Path] = {
    (for {
      router <- routes.toList
      method <- router.methods
    } yield {
      method.toString().toLowerCase -> Path.fromEndpoint(method, router, schemaToOpenApiSchema)
    }).toMap
  }
}
