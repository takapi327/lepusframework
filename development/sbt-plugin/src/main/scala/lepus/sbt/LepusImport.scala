/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.sbt

import sbt._

object LepusImport {

  def component(id: String) = "com.github.takapi327" %% id % lepus.core.LepusVersion.current

  val lepusCore = component("lepus")

  val lepusRouter = component("lepus-router")

  val lepusLogback = component("lepus-logback")

  val lepusServer = component("lepus-server")

  val lepusSwagger = component("lepus-swagger")
}

object LepusSwaggerImport {

  val swaggerTitle = SettingKey[String](
    label       = "swaggerTitle",
    description = "Title of the document generated by swagger"
  )

  val swaggerVersion = SettingKey[String](
    label       = "swaggerVersion",
    description = "Version of the document generated by swagger"
  )
}
