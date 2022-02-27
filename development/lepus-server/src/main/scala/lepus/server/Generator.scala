/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.server

import java.io.File
import java.nio.file.Files

import scala.io.Codec

object Generator {

  def generateServer(
    host:         String,
    port:         Int,
    routePackage: String,
    generatedDir: File
  ): File = {
    val file = new File(generatedDir, "LepusServer.scala")
    val scalaSource =
      s"""|/**
          | *  This file is part of the Lepus Framework.
          | *  For the full copyright and license information,
          | *  please view the LICENSE file that was distributed with this source code.
          | */
          |
          |package lepus.server
          |
          |import cats.effect._
          |
          |import org.http4s.blaze.server.BlazeServerBuilder
          |
          |object LepusServer extends IOApp {
          |
          |  def run(args: List[String]): IO[ExitCode] = {
          |    BlazeServerBuilder[IO]
          |      .bindHttp(${port}, "${host}")
          |      .withHttpApp(${routePackage}.routes)
          |      .withoutBanner
          |      .resource
          |      .use(_ => IO.never)
          |      .as(ExitCode.Success)
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
