/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import scala.concurrent.duration.DurationInt

import cats.effect.*
import cats.effect.implicits.*
import cats.effect.std.Console

import cats.implicits.*

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }

import doobie.*
import doobie.implicits.*

import lepus.logger.{ ExecLocation, LoggingIO, given }

/** A model for building a database. HikariCP construction, thread pool generation for database connection, test
  * connection, etc. are performed via the method.
  *
  * @param databaseConfig
  *   Configuration of database settings to be retrieved from Conf file
  * @param sync$F$0
  *   A type class that encodes the notion of suspending synchronous side effects in the F[_] context
  * @param async$F$1
  *   A type class that encodes the notion of suspending asynchronous side effects in the F[_] context
  * @param console$F$2
  *   Effect type agnostic Console with common methods to write to and read from the standard console. Suited only for
  *   extremely simple console input and output.
  * @tparam F
  *   the effect type.
  */
private[lepus] final case class DatabaseBuilder[F[_]: Sync: Async: Console](
  databaseConfig: DatabaseConfig
) extends LoggingIO[F]:

  /** Method for generating HikariDataSource with Resource.
    *
    * @param factory
    *   Process to generate HikariDataSource
    */
  private def createDataSourceResource(factory: => HikariDataSource): Resource[F, HikariDataSource] =
    val acquire = Sync[F].delay(factory)
    val release = (ds: HikariDataSource) => Sync[F].delay(ds.close())
    Resource.make(acquire)(release)

  /** Methods to build HikariCP, generate thread pool for database connection, build Transactor and test connections.
    */
  def resource: Resource[F, Transactor[F]] =
    (for
      hikariConfig <- buildConfig
      ec           <- ExecutionContexts.fixedThreadPool(hikariConfig.getMaximumPoolSize)
      datasource   <- createDataSourceResource(new HikariDataSource(hikariConfig))
    yield Transactor.fromDataSource[F](datasource, ec)).evalTap(testConnection)

  /** Method to generate Config for HikariCP.
    */
  private def buildConfig: Resource[F, HikariConfig] =
    Sync[F].delay {
      val hikariConfig = HikariConfigBuilder.default.makeFromDatabaseConfig(databaseConfig)
      hikariConfig.validate()
      hikariConfig
    }.toResource

  /** A method that tests the initialized database connection and attempts a wait connection at 5 second intervals until
    * a connection is available.
    *
    * @param xa
    *   A thin wrapper around a source of database connections, an interpreter, and a strategy for running programs,
    *   parameterized over a target monad M and an arbitrary wrapped value A. Given a stream or program in ConnectionIO
    *   or a program in Kleisli, a Transactor can discharge the doobie machinery and yield an effectful stream or
    *   program in M.
    */
  def testConnection(xa: Transactor[F]): F[Unit] =
    (testQuery(xa) >> logger.info(s"$databaseConfig Database connection test complete")).onError { (ex: Throwable) =>
      logger.warn(s"$databaseConfig Database not available, waiting 5 seconds to retry...", ex) >>
        Sync[F].sleep(5.seconds) >>
        testConnection(xa)
    }

  /** A query to be executed to check the connection to the database.
    *
    * @param xa
    *   A thin wrapper around a source of database connections, an interpreter, and a strategy for running programs,
    *   parameterized over a target monad M and an arbitrary wrapped value A. Given a stream or program in ConnectionIO
    *   or a program in Kleisli, a Transactor can discharge the doobie machinery and yield an effectful stream or
    *   program in M.
    */
  def testQuery(xa: Transactor[F]): F[Unit] =
    Sync[F].void(sql"select 1".query[Int].unique.transact(xa))

private[lepus] object DatabaseBuilder:
  def apply[F[_]: Sync: Async: Console](databaseConfig: DatabaseConfig): DatabaseBuilder[F] =
    new DatabaseBuilder[F](databaseConfig)
  def apply[F[_]: Sync: Async: Console](str: String): DatabaseBuilder[F] =
    new DatabaseBuilder[F](DatabaseConfig(str))
