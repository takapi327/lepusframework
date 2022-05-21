/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import scala.collection.immutable.ListMap

import cats.data.NonEmptyList

import io.circe.syntax._
import io.circe.yaml.Printer

import lepus.router.RouterConstructor
import lepus.swagger.model._

trait ExtensionMethods {

  implicit class SwaggerUIOps(swaggerUI: SwaggerUI)(implicit val encoder: io.circe.Encoder[SwaggerUI]) {
    def toYaml: String = Printer(dropNullKeys = true, preserveOrder = true).pretty(swaggerUI.asJson)
  }

  implicit class RouterConstructorsOps[F[_]](routes: NonEmptyList[RouterConstructor[F]]) {
    def toPathMap: Map[String, Path] = {
      (for {
        router <- routes.toList
        method <- router.methods
      } yield {
        method.toString().toLowerCase -> Path.fromEndpoint(method, router)
      }).toMap
    }
  }

  implicit class IterableToListMap[A](xs: Iterable[A]) {
    def toListMap[T, U](implicit ev: A <:< (T, U)): ListMap[T, U] = {
      val b = ListMap.newBuilder[T, U]
      for (x <- xs)
        b += x

      b.result()
    }
  }
}
