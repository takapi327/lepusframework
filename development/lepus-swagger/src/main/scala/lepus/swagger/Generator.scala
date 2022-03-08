/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.swagger

import java.io.File
import java.nio.file.Files

import scala.io.Codec

object Generator extends ExtensionMethods {

  def generateSwagger(
    title:         String,
    version:       String,
    baseDirectory: File
  ): File = {

    val outputFile = new File(baseDirectory, "LepusSwagger.scala")

    val scalaSource =
      s"""|/**
          | *  This file is part of the Lepus Framework.
          | *  For the full copyright and license information,
          | *  please view the LICENSE file that was distributed with this source code.
          | */
          |
          |package lepus.swagger
          |
          |import java.io.File
          |import java.nio.file.Files
          |
          |import scala.io.Codec
          |
          |import lepus.core.util.Configuration
          |import lepus.router.{ RouterProvider => LepusRouterProvider }
          |import Exception._
          |
          |object LepusSwagger extends ExtensionMethods {
          |
          |  type RouterProvider = LepusRouterProvider
          |
          |  private val SERVER_ROUTES = "lepus.router"
          |
          |  def main(args: Array[String]): Unit = generate()
          |
          |  def generate(): Unit = {
          |    val config = Configuration.load()
          |
          |    val file = new File("/tmp/", "LepusSwagger.yaml")
          |
          |    val routerProvider: RouterProvider = loadRouterProvider(config)
          |
          |    val groupEndpoint = routerProvider.routes.groupBy(_.endpoint.toPath)
          |    val endpoints     = groupEndpoint.map(v => (v._1 -> v._2.toPathMap))
          |    val swaggerUI     = SwaggerUI.build(Info("$title", "$version"), endpoints)
          |
          |    if (!file.exists()) {
          |      file.getParentFile.mkdirs()
          |      file.createNewFile()
          |    }
          |    Files.write(file.toPath, swaggerUI.toYaml.getBytes(implicitly[Codec].name))
          |  }
          |
          |  private def loadRouterProvider(config: Configuration): RouterProvider = {
          |    val routesClassName: String = config.get[String](SERVER_ROUTES)
          |    val routeClass: Class[_] =
          |      try ClassLoader.getSystemClassLoader.loadClass(routesClassName + "$$")
          |      catch {
          |        case ex: ClassNotFoundException =>
          |          throw GenerateSwaggerException(s"Couldn't find RouterProvider class '$$routesClassName'", Some(ex))
          |      }
          |
          |    if (!classOf[RouterProvider].isAssignableFrom(routeClass)) {
          |      throw GenerateSwaggerException(
          |        s\"\"\"
          |          |Class $${routeClass.getName} must implement RouterProvider interface
          |          |
          |          |RouterProvider must be imported and inherited by the $${routeClass.getName}
          |          |
          |          |import lepus.router.RouterProvider
          |          |
          |          |object $${routeClass.getName} extends RouterProvider
          |          |
          |          |\"\"\".stripMargin
          |      )
          |    }
          |
          |    val constructor =
          |      try routeClass.getField("MODULE$$").get(null).asInstanceOf[RouterProvider]
          |      catch {
          |        case ex: NoSuchMethodException =>
          |          throw GenerateSwaggerException(
          |            s"RouterProvider class $${routeClass.getName} must have a public default constructor",
          |            Some(ex)
          |          )
          |      }
          |
          |    constructor
          |  }
          |}
          |""".stripMargin

    if (!outputFile.exists()) {
      outputFile.getParentFile.mkdirs()
      outputFile.createNewFile()
    }

    Files.write(outputFile.toPath, scalaSource.getBytes(implicitly[Codec].name))

    outputFile
  }
}
