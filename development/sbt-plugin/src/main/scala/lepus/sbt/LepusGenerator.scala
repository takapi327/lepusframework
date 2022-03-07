/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.sbt

import scala.language.reflectiveCalls

import sbt._
import sbt.Keys._

import LepusInternalKeys._
import LepusSwaggerImport._

object LepusGenerator {

  def main(args: Array[String]): Unit = lepusGenerateSwagger(swaggerTitle, swaggerVersion, baseClassloader, baseDirectory, routePackage)

  private def convertToUrls(files: Seq[File]): Array[URL] = files.map(_.toURI.toURL).toArray

  def lepusGenerateSwagger(
    title:           SettingKey[String],
    version:         SettingKey[String],
    baseClassloader: TaskKey[ClassLoader],
    baseDirectory:   SettingKey[File],
    routePackage:    TaskKey[String]
  ): Def.Initialize[Task[Unit]] = Def.task {

    type Swagger = {
      def generateSwagger(
        title:           String,
        version:         String,
        baseClassloader: ClassLoader,
        baseDirectory:   File,
        routePackage:    String
      ): Unit
    }

    val projectClassLoader = new ProjectClassLoader(
      urls   = convertToUrls(lepusDependencyClasspath.value.files),
      parent = baseClassloader.value
    )

    val mainClass:  Class[_] = projectClassLoader.loadClass("lepus.swagger.Generator$")
    val mainObject: Swagger  = mainClass.getField("MODULE$").get(null).asInstanceOf[Swagger]

    mainObject.generateSwagger(
      title           = title.value,
      version         = version.value,
      baseClassloader = baseClassloader.value,
      baseDirectory   = baseDirectory.value,
      routePackage    = routePackage.value
    )
  }
}
