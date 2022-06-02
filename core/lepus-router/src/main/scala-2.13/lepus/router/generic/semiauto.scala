/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.generic

import language.experimental.macros

import magnolia1._

import lepus.router.model.Schema

/** Semi-automatic Schema derivation.
  *
  * This object provides helpers for creating [[lepus.router.model.Schema]] instances for case classes
  *
  * Typical usage will look like the following:
  *
  * {{{
  *   import lepus.router.model.Schema
  *   import lepus.router.generic.semiauto._
  *
  *   case class Foo(i: Int, p: (String, Double))
  *
  *   object Foo {
  *     implicit val schema: Schema[Foo] = deriveSchemer[Foo]
  *   }
  * }}}
  */
object semiauto extends SchemaDerivation {
  implicit def deriveSchemer[T]: Schema[T] = macro Magnolia.gen[T]
}
