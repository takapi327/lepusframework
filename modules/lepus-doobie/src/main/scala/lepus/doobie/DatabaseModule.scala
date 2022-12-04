/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext

import com.google.inject.name.Names
import com.google.inject.{ AbstractModule, TypeLiteral }

import cats.implicits.*
import cats.effect.{ IO, Sync, Async, Resource }

import lepus.guice.module.ResourceModule
import lepus.database.*
import DatabaseExecutionContexts.ThreadType
import lepus.hikari.HikariDatabaseBuilder
import lepus.logger.given

import lepus.doobie.implicits.*

/** Module for implicitly passing the Transactor generated for each Database to DoobieRepository.
  *
  * example:
  * {{{
  *   @Singleton
  *   class ToDoDatabase extends DatabaseModule:
  *     val databaseConfig: DatabaseConfig = DatabaseConfig("lepus.database://todo/writer")
  *
  *   // Configuration to conf file
  *   lepus.modules.enable += "ToDoDatabase"
  * }}}
  */
trait DatabaseModule extends HikariDatabaseBuilder[IO], ResourceModule[IO, Transactor[IO]]:

  /** List of keys in Config */
  private final val THREAD_POOL_TYPE: String = "thread_pool_type"
  private final val THREAD_POOL_SIZE: String = "thread_pool_size"

  /** If none, [[lepus.database.DatabaseConfig]] named is used. Default is none.
    */
  val named: Option[String] = None

  override val resource: Resource[IO, Transactor[IO]] =
    (for
      ds <- buildDataSource()
      ec <- buildExecutionContexts(ds.getMaximumPoolSize)(using databaseConfig)
    yield Transactor.fromDataSource[IO](ds, ec)).evalTap(testConnection)

  override private[lepus] lazy val build: Resource[IO, AbstractModule] =
    resource.map(v =>
      new AbstractModule:
        override def configure(): Unit =
          bind(new TypeLiteral[Transactor[IO]]() {})
            .annotatedWith(Names.named(named.getOrElse(databaseConfig.named)))
            .toInstance(v)
    )

  /** Methods for constructing ExecutionContexts of the specified format */
  private[lepus] def buildExecutionContexts(poolSize: Int)(using DatabaseConfig): Resource[IO, ExecutionContext] =
    getThreadPoolType.getOrElse(ThreadType.FIXED) match
      case ThreadType.FIXED  => DatabaseExecutionContexts.fixedThreadPool(getThreadPoolSize.getOrElse(poolSize))
      case ThreadType.CACHED => DatabaseExecutionContexts.cachedThreadPool

  /** Method to retrieve thread pool type information from the conf file. */
  private[lepus] def getThreadPoolType: DatabaseCF[Option[ThreadType]] =
    readConfig(_.get[Option[String]](THREAD_POOL_TYPE).flatMap(ThreadType.findByName))

  /** Method to retrieve thread pool size information from the conf file. */
  private[lepus] def getThreadPoolSize: DatabaseCF[Option[Int]] =
    readConfig(_.get[Option[Int]](THREAD_POOL_SIZE))

    /** A method that tests the initialized database connection and attempts a wait connection at 5 second intervals
      * until a connection is available.
      *
      * @param xa
      *   A thin wrapper around a source of database connections, an interpreter, and a strategy for running programs,
      *   parameterized over a target monad M and an arbitrary wrapped value A. Given a stream or program in
      *   ConnectionIO or a program in Kleisli, a Transactor can discharge the doobie machinery and yield an effectful
      *   stream or program in M.
      */
  def testConnection(xa: Transactor[IO]): IO[Unit] =
    (testQuery(xa) >> logger.info(s"$databaseConfig Database connection test complete")).onError { (ex: Throwable) =>
      logger.warn(s"$databaseConfig Database not available, waiting 5 seconds to retry...", ex) >>
        Sync[IO].sleep(5.seconds) >> testConnection(xa)
    }

  /** A query to be executed to check the connection to the database.
    *
    * @param xa
    *   A thin wrapper around a source of database connections, an interpreter, and a strategy for running programs,
    *   parameterized over a target monad M and an arbitrary wrapped value A. Given a stream or program in ConnectionIO
    *   or a program in Kleisli, a Transactor can discharge the doobie machinery and yield an effectful stream or
    *   program in M.
    */
  def testQuery(xa: Transactor[IO]): IO[Unit] =
    Sync[IO].void(sql"select 1".query[Int].unique.transact(xa))
