/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.server

import java.io.File

object LepusServer {

  def generate(
    host:         String,
    port:         Int,
    routePackage: String,
    generatedDir: File
  ): File = {

    Generator.generateServer(host, port, routePackage, generatedDir)
  }
}
