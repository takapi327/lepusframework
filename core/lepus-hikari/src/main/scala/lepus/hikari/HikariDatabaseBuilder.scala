/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.hikari

import scala.concurrent.ExecutionContext

import cats.effect.*
import cats.effect.implicits.*
import cats.effect.std.Console

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }

import lepus.database.{ DataSource, DatabaseConfig, DatabaseBuilder, DatabaseExecutionContexts }

/** A model for building a database. HikariCP construction, thread pool generation for database connection, test
  * connection, etc. are performed via the method.
  *
  * @param dataSource
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
private[lepus] final case class HikariDatabaseBuilder[F[_]: Sync: Async: Console](
  databases: Set[DatabaseConfig]
) extends DatabaseBuilder[F]:

  /** Method for generating HikariDataSource with Resource.
    *
    * @param factory
    *   Process to generate HikariDataSource
    */
  private def createDataSourceResource(factory: => HikariDataSource): Resource[F, HikariDataSource] =
    Resource.fromAutoCloseable(Sync[F].delay(factory))

  /** Method to generate Config for HikariCP.
    */
  private def buildConfig(dataSource: DataSource): Resource[F, HikariConfig] =
    Sync[F].delay {
      val hikariConfig = HikariConfigBuilder.default.makeFromDataSource(dataSource)
      hikariConfig.validate()
      hikariConfig
    }.toResource

  def build(): Resource[F, Map[DataSource, (ExecutionContext, HikariDataSource)]] =
    val default = Resource.eval(Sync[F].delay(Map.empty[DataSource, (ExecutionContext, HikariDataSource)]))
    databases.flatMap(_.dataSource.toList).foldLeft(default) { (_resource, db) =>
      for
        map              <- _resource
        hikariConfig     <- buildConfig(db)
        ec               <- DatabaseExecutionContexts.fixedThreadPool(hikariConfig.getMaximumPoolSize)
        hikariDataSource <- createDataSourceResource(new HikariDataSource(hikariConfig))
      yield map + (db -> (ec, hikariDataSource))
    }

private[lepus] object HikariDatabaseBuilder:
  def apply[F[_]: Sync: Async: Console](databases: Set[DatabaseConfig]): HikariDatabaseBuilder[F] =
    new HikariDatabaseBuilder[F](databases)
