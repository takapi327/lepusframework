/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie

import scala.concurrent.duration.DurationInt

import com.google.inject.name.Names
import com.google.inject.AbstractModule

import cats.effect.{ IO, Sync, Resource }

import lepus.guice.module.ResourceModule
import lepus.database.DatabaseConfig
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
trait DatabaseModule extends HikariDatabaseBuilder[IO], ResourceModule[ContextIO]:

  /** If none, [[lepus.database.DatabaseConfig]] named is used. Default is none.
    */
  val named: Option[String] = None

  override val resource: Resource[IO, ContextIO] =
    buildContext()
      .map(context => ContextIO(Transactor.fromDataSource[IO](context.ds, context.ec)))
      .evalTap(v => testConnection(v.xa))

  override private[lepus] lazy val build: Resource[cats.effect.IO, AbstractModule] =
    resource.map(v =>
      new AbstractModule:
        override def configure(): Unit =
          bind(classOf[ContextIO])
            .annotatedWith(Names.named(named.getOrElse(databaseConfig.named)))
            .toInstance(v)
    )

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
        IO.sleep(5.seconds) >> testConnection(xa)
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
