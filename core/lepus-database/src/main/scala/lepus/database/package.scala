/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import doobie.Transactor

/** Top-level imports provide aliases for the most commonly used types and modules.
  *
  * example:
  * {{{
  *   import lepus.database.*
  *   import lepus.database.implicits.*
  * }}}
  */
package object database extends LepusDoobie:
  type DatabaseCF[T] = DataSource ?=> T

  type DBTransactor[F[_]] = Map[DataSource, Transactor[F]]

  type Transact[F[_], T] = DBTransactor[F] ?=> T
