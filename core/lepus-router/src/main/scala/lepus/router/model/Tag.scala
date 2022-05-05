/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.model

/** Define an array of tags to be used to organize the API. Tags defined here will be displayed in the order in which
  * they are defined. It is not necessary to define all the tags used in the API, but the tags that are automatically
  * created will be added after the tags defined here. An untagged API will be assigned a tag named default.
  */
trait Tag {

  /** API tag name. Must be unique. */
  def name: String

  /** API tag description. */
  def description: Option[String] = None

  /** API tag external documents description. */
  def externalDocsDescription: Option[String] = None

  /** API tag external documents url. */
  def externalDocsUrl: Option[String] = None
}
