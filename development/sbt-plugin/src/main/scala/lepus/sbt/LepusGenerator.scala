/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.sbt

/**
 * reflective access of structural type member method run should be enabled by making the implicit value scala.language.
 * reflectiveCalls visible.
 * This can be achieved by adding the import clause 'import scala.language.reflectiveCalls' or by setting the compiler option -language:reflectiveCalls.
 * See the Scaladoc for value scala.language.reflectiveCalls for a discussion why the feature should be explicitly enabled.
 */
import scala.language.reflectiveCalls

import sbt._
import sbt.Keys._

import LepusInternalKeys._
import LepusImport.LepusKeys._
import LepusSwaggerImport._

object LepusGenerator {

  val generateServer = lepusGenerateServer(
    mainClassName = (Compile / run / mainClass),
    host          = defaultAddress,
    port          = defaultPort,
    routePackage  = routePackage,
    generatedDir  = (Compile / sourceManaged)
  )

  val generateSwagger = lepusGenerateSwagger(
    mainClassName = (Compile / run / mainClass),
    title         = swaggerTitle,
    version       = swaggerVersion,
    routePackage  = routePackage,
    generatedDir  = (Compile / sourceManaged)
  )

  def convertToUrls(files: Seq[File]): Array[URL] = files.map(_.toURI.toURL).toArray

  def lepusGenerateServer(
    mainClassName: TaskKey[Option[String]],
    host:          SettingKey[String],
    port:          SettingKey[Int],
    routePackage:  TaskKey[String],
    generatedDir:  SettingKey[File]
  ): Def.Initialize[Task[Seq[File]]] = Def.task {

    type Server = {
      def generate(
        host:         String,
        port:         Int,
        routePackage: String,
        generatedDir: File
      ): File
    }

    lazy val projectClassLoader = new ProjectClassLoader(
      urls   = convertToUrls(lepusDependencyClasspath.value.files),
      parent = baseClassloader.value
    )

    val mainClass: Class[_] = projectClassLoader.loadClass(mainClassName.value.getOrElse("lepus.server.LepusServer") + "$")
    val mainObject: Server = mainClass.getField("MODULE$").get(null).asInstanceOf[Server]

    Seq(mainObject.generate(
      host         = host.value,
      port         = port.value,
      routePackage = routePackage.value,
      generatedDir = generatedDir.value
    ))
  }

  def lepusGenerateSwagger(
    mainClassName: TaskKey[Option[String]],
    title:         SettingKey[String],
    version:       SettingKey[String],
    routePackage:  TaskKey[String],
    generatedDir:  SettingKey[File]
  ): Def.Initialize[Task[Seq[File]]] = Def.task {

    type Swagger = {
      def generateSwagger(
        title:        String,
        version:      String,
        routePackage: String,
        generatedDir: File
      ): File
    }

    lazy val projectClassLoader = new ProjectClassLoader(
      urls   = convertToUrls(lepusDependencyClasspath.value.files),
      parent = baseClassloader.value
    )

    val mainClass: Class[_] = projectClassLoader.loadClass("lepus.swagger.Generator$")
    val mainObject: Swagger = mainClass.getField("MODULE$").get(null).asInstanceOf[Swagger]

    Seq(mainObject.generateSwagger(
      title        = title.value,
      version      = version.value,
      routePackage = routePackage.value,
      generatedDir = generatedDir.value
    ))
  }
}
