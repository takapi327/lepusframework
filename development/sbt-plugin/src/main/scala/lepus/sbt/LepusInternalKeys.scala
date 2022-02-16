/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.sbt

import sbt._
import sbt.Keys._

object LepusInternalKeys {

  val baseClassloader = taskKey[ClassLoader](
    "The base classloader"
  )

  val externalizedResources = TaskKey[Seq[(File, String)]](
    label       = "externalizedResources",
    description = "The resources to externalize"
  )

  val lepusDependencyClasspath = TaskKey[Classpath](
    label       = "lepusDependencyClasspath",
    description = "The classpath containing all the jar dependencies of the project"
  )
}
