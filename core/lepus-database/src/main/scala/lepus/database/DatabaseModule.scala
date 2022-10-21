/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

/** Module for implicitly passing the Transactor generated for each Database to DoobieRepository.
  *
  * @tparam F
  *   the effect type.
  *
  * example:
  * {{{
  *   case class ToDoDatabase(
  *     database: DatabaseConfig
  *   )(using DBTransactor[F]) extends DatabaseModule[F]
  * }}}
  */
trait DatabaseModule[F[_]](using dbt: DBTransactor[F]):

  protected val database: DatabaseConfig

  private[lepus] def transactor(key: String): Transactor[F] = database.dataSource
    .find(_.replication.contains(key))
    .flatMap(ds => dbt.get(ds))
    .getOrElse(throw new IllegalStateException(s"$database database is not registered."))
