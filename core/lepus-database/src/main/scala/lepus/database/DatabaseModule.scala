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
  *     database:  DatabaseConfig,
  *     defaultDB: String
  *   )(using DBTransactor[F]) extends DatabaseModule[F]
  *
  *   object ToDoDatabase:
  *     val db: DatabaseConfig = DatabaseConfig("lepus.database://todo", NonEmptyList.of("master", "slave"))
  *
  *     given Transact[IO, ToDoDatabase] = ToDoDatabase(db, "slave")
  *
  *     val taskRepository: Transact[IO, TaskRepository] = TaskRepository()
  *     etc...
  * }}}
  */
trait DatabaseModule[F[_]](using dbt: DBTransactor[F]):

  /** Value with configuration to establish a connection to Database */
  protected val database: DatabaseConfig

  /** Database used by default This value must be contained in the string passed to the replication parameter of
    * [[DatabaseConfig]]
    */
  def defaultDB: String

  /** Method to retrieve the Transactor corresponding to the database replication you wish to specify */
  private[lepus] def transactor(key: String): Transactor[F] = database.dataSource
    .find(_.replication.contains(key))
    .flatMap(ds => dbt.get(ds))
    .getOrElse(throw new IllegalStateException(s"$database database is not registered."))
