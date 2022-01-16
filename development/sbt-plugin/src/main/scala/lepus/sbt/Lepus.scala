/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.sbt

import sbt._

import com.typesafe.sbt.packager.archetypes.JavaServerAppPackaging

object LepusServer extends AutoPlugin {
  override def requires = JavaServerAppPackaging
  val autoImport = LepusImport

  override def projectSettings = LepusSettings.serverSettings
}

object Lepus extends AutoPlugin {
  override def requires = LepusServer
}