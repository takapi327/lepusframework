/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie

import cats.effect.Async

import lepus.database.DatabaseConfig
import lepus.hikari.HikariContext

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
  *   )(using LepusContext) extends DatabaseModule[F]
  *
  *   object ToDoDatabase:
  *     val db: DatabaseConfig = DatabaseConfig("lepus.database://todo", NonEmptyList.of("master", "slave"))
  *
  *     given Transact[ToDoDatabase] = ToDoDatabase(db, "slave")
  *
  *     val taskRepository: Transact[TaskRepository] = TaskRepository()
  *     etc...
  * }}}
  */
trait DatabaseModule[F[_]: Async](using context: HikariContext):

  /** Value with configuration to establish a connection to Database */
  protected val database: DatabaseConfig

  /** Database used by default This value must be contained in the string passed to the replication parameter of
    * [[DatabaseConfig]]
    */
  def defaultDB: String

  /** Method to retrieve the Transactor corresponding to the database replication you wish to specify */
  private[lepus] val transactor: String => Transactor[F] = (key: String) =>
    database.dataSource
      .find(_.replication.contains(key))
      .flatMap(ds => context.get(ds).map(ctx => Transactor.fromDataSource[F](ctx.ds, ctx.ec)))
      .getOrElse(throw new IllegalStateException(s"$database database is not registered."))
