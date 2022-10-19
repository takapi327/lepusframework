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

  def database: DatabaseConfig

  private[lepus] lazy val transactor: Transactor[F] =
    require(database.dataSource.length == 1, "If you are replicating to a database, you must specify the access point")
    database.dataSource.headOption
      .flatMap(ds => {
        dbt
          .get(ds)
          .orElse(
            dbt
              .flatMap((db, xa) => {
                if db equals ds then Some(xa) else None
              })
              .headOption
          )
      })
      .getOrElse(throw new IllegalStateException(s"$database database is not registered."))

  private[lepus] def transactor(key: String): Transactor[F] = database.dataSource
    .find(_.replication.contains(key))
    .flatMap(ds => {
      dbt.get(ds)
    })
    .getOrElse(throw new IllegalStateException(s"$database database is not registered."))
