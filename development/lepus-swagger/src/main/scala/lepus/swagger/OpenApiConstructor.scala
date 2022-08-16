/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import lepus.router.RouterConstructor

import lepus.swagger.model.Tag

trait OpenApiConstructor[F[_], T]:
  self: RouterConstructor[F, T] =>

  /** Summary of this endpoint, used during Open API document generation. */
  def summary: Option[String] = None

  /** Description of this endpoint, used during Open API document generation. */
  def description: Option[String] = None

  /** Tag of this endpoint, used during Open API document generation. */
  def tags: Set[Tag] = Set.empty[Tag]

  /** A flag used during Open API document generation to indicate whether this endpoint is deprecated or not.
    */
  def deprecated: Option[Boolean] = None
