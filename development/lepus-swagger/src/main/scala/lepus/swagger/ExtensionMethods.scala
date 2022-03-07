/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.swagger

import cats.data.NonEmptyList

import io.circe.syntax._
import io.circe.yaml.Printer

import lepus.router.model.Endpoint
import lepus.swagger.model._

trait ExtensionMethods {

  implicit class SwaggerUIOps(swaggerUI: SwaggerUI) {
    def toYaml: String = Printer(dropNullKeys = true, preserveOrder = true).pretty(swaggerUI.asJson)
  }

  implicit class LepusEndpointOps(endpoint: Endpoint) {
    def toPath: String = "/" + endpoint.endpoint.asVector().map(_.toPath()).mkString("/")
  }

  implicit class ServerRouteOps[F[_]](serverRoutes: NonEmptyList[lepus.router.ServerRoute[F, _]]) {
    def toPathMap: Map[String, Path] = {
      (for {
        serverRoute <- serverRoutes.toList
        method      <- serverRoute.methods
      } yield {
        (method.toString().toLowerCase -> Path.fromEndpoint(serverRoute.endpoint))
      }).toMap
    }
  }
}
