/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.database

import scala.concurrent.duration.DurationInt

import cats.effect.*
import cats.effect.implicits.*
import cats.effect.std.Console

import cats.implicits.*

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import doobie.*
import doobie.implicits.*

import lepus.logger.{ExecLocation, LoggingIO, given}

private[lepus] final case class Database[F[_]: Sync: Async: Console](
  databaseConfig: DatabaseConfig
) extends LoggingIO[F]:

  private def createDataSourceResource(factory: => HikariDataSource): Resource[F, HikariDataSource] =
    val acquire = Sync[F].delay(factory)
    val release = (ds: HikariDataSource) => Sync[F].delay(ds.close())
    Resource.make(acquire)(release)

  def resource: Resource[F, Transactor[F]] =
    (for
      hikariConfig <- buildConfig
      ec           <- ExecutionContexts.fixedThreadPool(hikariConfig.getMaximumPoolSize)
      datasource   <- createDataSourceResource(new HikariDataSource(hikariConfig))
    yield Transactor.fromDataSource[F](datasource, ec)).evalTap(testConnection)

  private def buildConfig: Resource[F, HikariConfig] =
    Sync[F].delay {
      val hikariConfig = HikariConfigBuilder.default.makeFromDatabaseConfig(databaseConfig)
      hikariConfig.validate()
      hikariConfig
    }.toResource

  def testConnection(xa: Transactor[F]): F[Unit] =
    (testQuery(xa) >> logger.info(s"$databaseConfig Database connection test complete")).onError {
      (ex: Throwable) =>
        logger.warn(s"$databaseConfig Database not available, waiting 5 seconds to retry...", ex) >>
          Sync[F].sleep(5.seconds) >>
          testConnection(xa)
    }

  def testQuery(xa: Transactor[F]): F[Unit] =
    Sync[F].void(sql"select 1".query[Int].unique.transact(xa))

private[lepus] object Database:
  def apply[F[_]: Sync: Async: Console](databaseConfig: DatabaseConfig): Database[F] =
    new Database[F](databaseConfig)
  def apply[F[_]: Sync: Async: Console](str: String): Database[F] =
    new Database[F](DatabaseConfig(str))
