/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

private[lepus] object Exception:

  /** Indicates an issue with generate a swagger api document, e.g. a problem reading its configuration.
    */
  final case class GenerateSwaggerException(message: String, throwableOpt: Option[Throwable] = None)
    extends Exception(message, throwableOpt.orNull)
