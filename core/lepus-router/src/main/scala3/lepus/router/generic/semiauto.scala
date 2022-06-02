/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router.generic

import scala.deriving.Mirror

import magnolia1.*

import lepus.router.*
import model.Schema

/**
 * Semi-automatic Schema derivation.
 *
 * This object provides helpers for creating [[lepus.router.model.Schema]]  instances for case classes
 *
 * Typical usage will look like the following:
 *
 * {{{
 *   import lepus.router.model.Schema
 *   import lepus.router.generic.semiauto.*
 *
 *   case class Foo(i: Int, p: (String, Double))
 *
 *   object Foo {
 *     given schema: Schema[Foo] = deriveSchemer[Foo]
 *   }
 * }}}
 */
object semiauto extends SchemaDerivation:
  inline final def deriveSchemer[T](using inline M: Mirror.Of[T]): Schema[T] = derived[T]
