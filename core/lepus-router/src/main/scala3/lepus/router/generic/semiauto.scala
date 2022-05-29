/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router.generic

import scala.deriving.Mirror

import magnolia1.*

import lepus.router.*
import model.Schema

object semiauto extends SchemaDerivation:
  inline final def deriveSchemer[T](using inline M: Mirror.Of[T]): Schema[T] = derived[T]
