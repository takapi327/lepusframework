/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import scala.collection.immutable.ListMap
import io.circe.Encoder
import io.circe.syntax._
import io.circe.yaml.Printer
import lepus.swagger.model.OpenApiUI

trait ExtensionMethods {

  implicit class OpenApiUIOps(openApiUI: OpenApiUI)(implicit encoder: Encoder[OpenApiUI]) {
    def toYaml: String = Printer(dropNullKeys = true, preserveOrder = true).pretty(openApiUI.asJson)
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
