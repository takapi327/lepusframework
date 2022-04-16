/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.model

/**
 * Classification by API tags
 */
trait Tag {

  /** API tag name. Must be unique. */
  def name: String

  /** API tag description. */
  def description: Option[String] = None

  /** API tag external documents. */
  def externalDocs: Option[ExternalDoc] = None
}

/**
 * External Documentation
 */
trait ExternalDoc {

  /** API tag external documents description. */
  def description: Option[String] = None

  /** API tag external documents url. */
  def url: Option[String] = None
}
