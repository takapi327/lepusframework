/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.hikari

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

import org.specs2.mutable.Specification

import lepus.database.DataSource

object HikariConfigBuilderTest extends Specification, HikariConfigBuilder:

  "Testing the HikariConfigBuilder" should {

    "The value of the specified key can be retrieved from the conf file by setting lepus.hikaricp://database" in {
      given DataSource = DataSource("lepus.hikaricp", "database", "write")

      val catalog = readConfig(_.get[Option[String]]("catalog"))
      catalog.nonEmpty && catalog.get == "lepus"
    }

    "The value of the specified key can be retrieved from the conf file by setting lepus://database" in {
      given DataSource = DataSource("lepus", "database", "write")

      val poolName = readConfig(_.get[Option[String]]("pool_name"))
      poolName.nonEmpty && poolName.get == "lepus-pool"
    }

    "The value of the specified key can be retrieved from the conf file by setting lepus://database/write" in {
      given DataSource = DataSource("lepus", "database", "write")

      val jdbcUrl = readConfig(_.get[Option[String]]("jdbc_url"))
      jdbcUrl.nonEmpty && jdbcUrl.get == "jdbc:mysql://127.0.0.1:3306/lepus"
    }

    "IllegalArgumentException exception is raised when transaction_isolation is set to a value other than expected" in {
      given DataSource = DataSource("lepus", "database", "failure")

      getTransactionIsolation must throwAn[IllegalArgumentException]
    }

    "The catalog setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getCatalog == "lepus"
    }

    "The connection_timeout setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getConnectionTimeout == Duration(30, TimeUnit.SECONDS).toMillis
    }

    "The idle_timeout setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getIdleTimeout == Duration(10, TimeUnit.MINUTES).toMillis
    }

    "The leak_detection_threshold setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getLeakDetectionThreshold == Duration.Zero.toMillis
    }

    "The maximum_pool_size setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getMaximumPoolSize == 32
    }

    "The max_lifetime setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getMaxLifetime == Duration(30, TimeUnit.MINUTES).toMillis
    }

    "The minimum_idle setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getMinimumIdle == 10
    }

    "The pool_name setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getPoolName == "lepus-pool"
    }

    "The validation_timeout setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getValidationTimeout == Duration(5, TimeUnit.SECONDS).toMillis
    }

    "The connection_init_sql setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getConnectionInitSql == "select 1"
    }

    "The connection_test_query setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getConnectionTestQuery == "select 1"
    }

    "The connection_test_query setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getDataSourceClassName == "com.mysql.cj.jdbc.Driver"
    }

    "The datasource_jndi setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getDataSourceJNDI == ""
    }

    "The initialization_fail_timeout setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getInitializationFailTimeout == Duration(1, TimeUnit.MILLISECONDS).toMillis
    }

    "The jdbc_url setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getJdbcUrl == "jdbc:mysql://127.0.0.1:3306/lepus"
    }

    "The schema setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getSchema == "lepus"
    }

    "The username setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getUsername == "lepus"
    }

    "The password setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getPassword == "mysql"
    }

    "The transaction_isolation setting in HikariConfig matches the setting in the conf file" in {
      val config = makeFromDataSource(DataSource("lepus.hikaricp", "database", "write"))

      config.getTransactionIsolation == "TRANSACTION_NONE"
    }
  }
