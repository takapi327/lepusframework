/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.sbt

import scala.language.reflectiveCalls

import sbt._
import sbt.Keys._

import LepusInternalKeys._
import LepusSwaggerImport._

object LepusGenerator {

  val generateSwagger = lepusGenerateSwagger(
    title         = swaggerTitle,
    version       = swaggerVersion,
    sourceManaged = Compile / sourceManaged,
    baseDirectory = Compile / baseDirectory
  )

  private def convertToUrls(files: Seq[File]): Array[URL] = files.map(_.toURI.toURL).toArray

  def lepusGenerateSwagger(
    title:         SettingKey[String],
    version:       SettingKey[String],
    sourceManaged: SettingKey[File],
    baseDirectory: SettingKey[File]
  ): Def.Initialize[Task[Seq[File]]] = Def.task {

    type Swagger = {
      def generateSwagger(
        title:         String,
        version:       String,
        sourceManaged: File,
        baseDirectory: File
      ): File
    }

    val projectClassLoader = new ProjectClassLoader(
      urls   = convertToUrls(lepusDependencyClasspath.value.files),
      parent = baseClassloader.value
    )

    val mainClass:  Class[_] = projectClassLoader.loadClass("lepus.swagger.OpenApiGenerator$")
    val mainObject: Swagger  = mainClass.getField("MODULE$").get(null).asInstanceOf[Swagger]

    Seq(
      mainObject.generateSwagger(
        title         = title.value,
        version       = version.value,
        sourceManaged = sourceManaged.value,
        baseDirectory = baseDirectory.value
      )
    )
  }
}
