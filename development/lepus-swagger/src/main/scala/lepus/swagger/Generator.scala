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
    title:        String,
    version:      String,
    routePackage: String,
    generatedDir: File
  ): File = {
    val file = new File(generatedDir, "LepusSwagger.scala")

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
          |object LepusSwagger extends ExtensionMethods {
          |
          |  def main(args: Array[String]): Unit = generate()
          |
          |  def generate(): Unit = {
          |    val file          = new File("/tmp/", "LepusSwagger.yaml")
          |    val groupEndpoint = $routePackage.routesTest.groupBy(_.endpoint.toPath)
          |    val endpoints     = groupEndpoint.map(v => (v._1 -> v._2.toPathMap))
          |    val swaggerUI     = SwaggerUI.build(Info("$title", "$version"), endpoints)
          |
          |    if (!file.exists()) {
          |      file.getParentFile.mkdirs()
          |      file.createNewFile()
          |    }
          |
          |    Files.write(file.toPath, swaggerUI.toYaml.getBytes(implicitly[Codec].name))
          |  }
          |
          |}
          |""".stripMargin

    if (!file.exists()) {
      file.getParentFile.mkdirs()
      file.createNewFile()
    }

    Files.write(file.toPath, scalaSource.getBytes(implicitly[Codec].name))

    file
  }
}
