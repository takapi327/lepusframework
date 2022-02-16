/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.sbt

import sbt._

object LepusImport {

  def component(id: String) = "com.github.takapi327" %% id % lepus.core.LepusVersion.current

  val lepusCore = component("lepus")

  val lepusRouter = component("lepus-router")

  val lepusServer = component("lepus-server")

  object LepusKeys {

    val routePackage = TaskKey[String](
      label       = "routePackage",
      description = "Package of server routing information"
    )

    val defaultPort = SettingKey[Int](
      label       = "defaultPort",
      description = "The default port that Server runs on"
    )

    val defaultAddress = SettingKey[String](
      label       = "defaultAddress",
      description = "The default address that Server runs on"
    )
  }
}
