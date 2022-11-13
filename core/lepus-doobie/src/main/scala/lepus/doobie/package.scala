/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import doobie.Transactor

import lepus.database.DataSource
import lepus.hikari.HikariContext

/** Top-level imports provide aliases for the most commonly used types and modules.
  *
  * example:
  * {{{
  *   import lepus.doobie.*
  *   import lepus.doobie.implicits.*
  * }}}
  */
package object doobie extends LepusDoobie:

  type DBTransactor[F[_]] = Map[DataSource, Transactor[F]]

  type Transact[T] = HikariContext ?=> T
