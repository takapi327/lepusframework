/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import io.circe.Encoder
import io.circe.syntax.*
import io.circe.yaml.Printer

import lepus.core.internal.ExtensionMethods as BaseExtensionMethods
import lepus.swagger.model.OpenApiUI

trait ExtensionMethods extends BaseExtensionMethods:

  extension (openApiUI: OpenApiUI)(using Encoder[OpenApiUI])
    def toYaml: String = Printer(dropNullKeys = true, preserveOrder = true).pretty(openApiUI.asJson)
