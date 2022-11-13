/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie

import cats.effect.Async

import lepus.database.DatabaseConfig
import lepus.hikari.LepusContext

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
trait DatabaseModule[F[_]: Async](using context: LepusContext):

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
      .flatMap(ds => context.get(ds).map((ec, datasource) => Transactor.fromDataSource[F](datasource, ec)))
      .getOrElse(throw new IllegalStateException(s"$database database is not registered."))

  /** A method that tests the initialized database connection and attempts a wait connection at 5 second intervals until
    * a connection is available.
    *
    * @param xa
    *   A thin wrapper around a source of database connections, an interpreter, and a strategy for running programs,
    *   parameterized over a target monad M and an arbitrary wrapped value A. Given a stream or program in ConnectionIO
    *   or a program in Kleisli, a Transactor can discharge the doobie machinery and yield an effectful stream or
    *   program in M.
    */
  // def testConnection(xa: Transactor[F]): F[Unit] =
  //  (testQuery(xa) >> logger.info(s"$dataSource Database connection test complete")).onError { (ex: Throwable) =>
  //    logger.warn(s"$dataSource Database not available, waiting 5 seconds to retry...", ex) >>
  //      Sync[F].sleep(5.seconds) >>
  //      testConnection(xa)
  //  }

  /** A query to be executed to check the connection to the database.
    *
    * @param xa
    *   A thin wrapper around a source of database connections, an interpreter, and a strategy for running programs,
    *   parameterized over a target monad M and an arbitrary wrapped value A. Given a stream or program in ConnectionIO
    *   or a program in Kleisli, a Transactor can discharge the doobie machinery and yield an effectful stream or
    *   program in M.
    */
  // def testQuery(xa: Transactor[F]): F[Unit] =
  //  Sync[F].void(sql"select 1".query[Int].unique.transact(xa))
