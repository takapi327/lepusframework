/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.swagger

import lepus.router.RouterConstructor
import lepus.router.http.Method

import lepus.swagger.model.Tag

trait OpenApiConstructor[F[_], P]:
  self: RouterConstructor[F, P] =>

  /** Summary of this endpoint, used during Open API document generation. */
  def summary: Option[String] = None

  /** Description of this endpoint, used during Open API document generation. */
  def description: Option[String] = None

  /** Tag of this endpoint, used during Open API document generation. */
  def tags: Set[Tag] = Set.empty[Tag]

  /** A flag used during Open API document generation to indicate whether this endpoint is deprecated or not.
   */
  def deprecated: Option[Boolean] = None

  /** List of methods that can be handled by this endpoint. */
  final lazy val methods: List[Method] = Method.values.filter(self.routes.isDefinedAt).toList
