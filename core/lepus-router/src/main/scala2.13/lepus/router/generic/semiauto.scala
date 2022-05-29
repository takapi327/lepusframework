/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router.generic

import language.experimental.macros

import magnolia1._

import lepus.router.model.Schema

object semiauto extends SchemaDerivation {
  implicit def deriveSchemer[T]: Schema[T] = macro Magnolia.gen[T]
}
