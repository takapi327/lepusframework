/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.sbt

import java.net.URL

import sbt._

/**
 * A collection of tools to validate the Sbt plugin.
 */
object ScriptedTools extends AutoPlugin {

  override def trigger = allRequirements

  private val TIMEOUT_NUMBER = 10000

  /**
   * Methods for calling the api of the specified path.
   *
   * @param path
   * The path of the API to call.
   * @return
   * The response status and InputStream value resulting from the API call.
   */
  def callUrl(path: String): (Int, String) = {
    callUrl("localhost", 5555, path)
  }

  /**
   * Method to invoke api for the specified host and port and path.
   *
   * @param host
   * The host of the API to call.
   * @param port
   * The port of the API to call.
   * @param path
   * The path of the API to call.
   * @return
   * The response status and InputStream value resulting from the API call.
   */
  def callUrl(
    host: String,
    port: Int,
    path: String
  ): (Int, String) = {
    callUrlImpl(url(s"http://$host:$port/$path"))
  }

  /**
   * Method to call the api of the specified URL.
   *
   * @param url
   * The url of the API to call.
   * @return
   * The response status and InputStream value resulting from the API call.
   */
  def callUrlImpl(url: URL): (Int, String) = {
    val connection = url.openConnection().asInstanceOf[java.net.HttpURLConnection]
    connection.setConnectTimeout(TIMEOUT_NUMBER)
    connection.setReadTimeout(TIMEOUT_NUMBER)
    try {
      val status = connection.getResponseCode
      val input  = if (status < 400) connection.getInputStream else connection.getErrorStream
      val contents =
        if (input == null) ""
        else {
          try IO.readStream(input)
          finally input.close()
        }
      (status, contents)
    } finally connection.disconnect()
  }
}
