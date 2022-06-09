/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logback

import scala.Console._

trait ColorLog {

  private lazy val isANSISupported = {
    sys.props
      .get("sbt.log.noformat")
      .map(_ != "true")
      .orElse {
        sys.props
          .get("os.name")
          .map(_.toLowerCase(java.util.Locale.ENGLISH))
          .filter(_.contains("windows"))
          .map(_ => false)
      }
      .getOrElse(true)
  }

  def red(str: String): String     = if (isANSISupported) RED + str + RESET else str
  def blue(str: String): String    = if (isANSISupported) BLUE + str + RESET else str
  def cyan(str: String): String    = if (isANSISupported) CYAN + str + RESET else str
  def green(str: String): String   = if (isANSISupported) GREEN + str + RESET else str
  def magenta(str: String): String = if (isANSISupported) MAGENTA + str + RESET else str
  def white(str: String): String   = if (isANSISupported) WHITE + str + RESET else str
  def black(str: String): String   = if (isANSISupported) BLACK + str + RESET else str
  def yellow(str: String): String  = if (isANSISupported) YELLOW + str + RESET else str
}
