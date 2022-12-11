/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import java.io.File
import java.nio.file.Files

import scala.io.Codec

import cats.effect.IO

import lepus.core.util.Configuration
import lepus.router.LepusRouter

import Exception.GenerateSwaggerException

private[lepus] object OpenApiGenerator extends ExtensionMethods:

  private val SERVER_ROUTES = "lepus.server.routes"

  def generateSwagger(
    title:         String,
    version:       String,
    sourceManaged: File,
    baseDirectory: File
  ): File =

    val outputFile = new File(sourceManaged, "LepusOpenApi.scala")

    val scalaSource =
      s"""|/**
          | *  This file is part of the Lepus Framework.
          | *  For the full copyright and license information,
          | *  please view the LICENSE file that was distributed with this source code.
          | */
          |
          |${ indent(0)(`package`) }
          |${ indent(0)(imports) }
          |
          |private[lepus] object LepusOpenApi extends OpenApiEncoder, ExtensionMethods:
          |
          |  def main(args: Array[String]): Unit = generate()
          |
          |  def generate(): Unit =
          |    val config = Configuration.load()
          |
          |    val file = new File("$baseDirectory/docs/", "OpenApi.yaml")
          |
          |    val lepusRouter: LepusRouter[IO] = OpenApiGenerator.loadLepusRouter(config)
          |
          |    val openAPIUI = RouterToOpenAPI.generateOpenAPIDocs[IO](Info("$title", "$version"), lepusRouter)
          |
          |    if !file.exists() then
          |      file.getParentFile.mkdirs()
          |      file.createNewFile()
          |
          |    Files.write(file.toPath, openAPIUI.toYaml.getBytes(summon[Codec].name))
          |
          |""".stripMargin

    if !outputFile.exists() then
      outputFile.getParentFile.mkdirs()
      outputFile.createNewFile()
      Files.write(outputFile.toPath, scalaSource.getBytes(summon[Codec].name))

    outputFile

  private[lepus] def indent(i: Int)(str: String): String =
    str.linesIterator.map(" " * i + _).mkString("\n")

  private[lepus] val `package`: String = "package lepus.swagger"

  private[lepus] val imports: String =
    """
     |import java.io.File
     |import java.nio.file.Files
     |import scala.io.Codec
     |import cats.effect.IO
     |import lepus.core.util.Configuration
     |import lepus.router.LepusRouter
     |import lepus.swagger.model.Info
     |import lepus.swagger.{ RouterToOpenAPI, OpenApiEncoder }
     |import Exception.GenerateSwaggerException
     |""".stripMargin

  /** Obtain routing information from the executing application.
    *
    * @param config
    *   Configuration to obtain the configuration of the running application
    * @return
    *   Value of LepusRouter registered in the app
    */
  private[lepus] def loadLepusRouter(config: Configuration): LepusRouter[IO] =
    val routesClassName: String = config.get[String](SERVER_ROUTES)
    val routeClass: Class[_] =
      try ClassLoader.getSystemClassLoader.loadClass(routesClassName + "$")
      catch
        case ex: ClassNotFoundException =>
          throw GenerateSwaggerException(s"Couldn't find LepusApp class '$routesClassName'", Some(ex))

    if !classOf[LepusRouter[IO]].isAssignableFrom(routeClass) then
      throw GenerateSwaggerException(
        s"""
          |Class ${ routeClass.getName } must implement LepusApp interface
          |
          |LepusApp must be imported and inherited by the ${ routeClass.getName }
          |
          |import lepus.router.LepusRouter
          |
          |object ${ routeClass.getName } extends LepusRouter[IO]
          |
          |""".stripMargin
      )

    val constructor =
      try routeClass.getField("MODULE$").get(null).asInstanceOf[LepusRouter[IO]]
      catch
        case ex: NoSuchMethodException =>
          throw GenerateSwaggerException(
            s"LepusRouter class ${ routeClass.getName } must have a public default constructor",
            Some(ex)
          )

    constructor
