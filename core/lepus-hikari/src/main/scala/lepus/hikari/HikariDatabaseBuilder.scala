/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.hikari

import cats.effect.*
import cats.effect.implicits.*
import cats.effect.std.Console

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }

import lepus.database.*

/** A model for building a database. HikariCP construction, thread pool generation for database connection, test
  * connection, etc. are performed via the method.
  *
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
private[lepus] trait HikariDatabaseBuilder[F[_]: Sync: Async: Console]
  extends HikariConfigBuilder,
          DatabaseBuilder[F, HikariDataSource]:

  protected val databaseConfig: DatabaseConfig

  /** Method for generating HikariDataSource with Resource.
    *
    * @param factory
    *   Process to generate HikariDataSource
    */
  private def createDataSourceResource(factory: => HikariDataSource): Resource[F, HikariDataSource] =
    Resource.fromAutoCloseable(Sync[F].delay(factory))

  /** Method to generate Config for HikariCP.
    */
  private def buildConfig(): Resource[F, HikariConfig] =
    Sync[F].delay {
      val hikariConfig = makeFromDatabaseConfig(databaseConfig)
      hikariConfig.validate()
      hikariConfig
    }.toResource

  def buildContext(): Resource[F, HikariDataSource] =
    for
      hikariConfig     <- buildConfig()
      hikariDataSource <- createDataSourceResource(new HikariDataSource(hikariConfig))
    yield hikariDataSource
