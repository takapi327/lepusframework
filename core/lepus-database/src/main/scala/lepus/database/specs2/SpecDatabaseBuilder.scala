/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database.specs2

import cats.effect.{ Resource, Sync, Async }

import lepus.database.*

/** Trait to assist with DB connection testing
  *
  * @tparam F
  *   the effect type.
  */
trait SpecDatabaseBuilder[F[_]: Sync: Async] extends DriverBuilder:

  def database: DatabaseConfig

  private def dataSources: DBTransactor[F] =
    database.dataSource.map(ds => ds -> makeFromDataSource[F](ds)).toMap

  given DBTransactor[F] = dataSources
