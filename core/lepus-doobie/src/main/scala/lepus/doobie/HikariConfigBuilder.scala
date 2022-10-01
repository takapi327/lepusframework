/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie

import java.util.Properties
import java.util.concurrent.{ ScheduledExecutorService, ThreadFactory, TimeUnit }
import javax.sql.DataSource

import scala.util.Try
import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters.*

import lepus.core.util.Configuration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.metrics.MetricsTrackerFactory

trait HikariConfigBuilder:

  protected val config: Configuration = Configuration.load()

  final private val CATALOG                     = "catalog"
  final private val CONNECTION_TIMEOUT          = "connection_timeout"
  final private val IDLE_TIMEOUT                = "idle_timeout"
  final private val LEAK_DETECTION_THRESHOLD    = "leak_detection_threshold"
  final private val MAXIMUM_POOL_SIZE           = "maximum_pool_size"
  final private val MAX_LIFETIME                = "max_lifetime"
  final private val MINIMUM_IDLE                = "minimum_idle"
  final private val POOL_NAME                   = "pool_name"
  final private val VALIDATION_TIMEOUT          = "validation_timeout"
  final private val ALLOW_POOL_SUSPENSION       = "allow_pool_suspension"
  final private val AUTO_COMMIT                 = "auto_commit"
  final private val CONNECTION_INIT_SQL         = "connection_init_sql"
  final private val CONNECTION_TEST_QUERY       = "connection_test_query"
  final private val DATA_SOURCE_CLASSNAME       = "data_source_classname"
  final private val DATASOURCE_JNDI             = "datasource_jndi"
  final private val INITIALIZATION_FAIL_TIMEOUT = "initialization_fail_timeout"
  final private val ISOLATE_INTERNAL_QUERIES    = "isolate_internal_queries"
  final private val JDBC_URL                    = "jdbc_url"
  final private val READONLY                    = "readonly"
  final private val REGISTER_MBEANS             = "register_mbeans"
  final private val SCHEMA                      = "schema"
  final private val USERNAME                    = "username"
  final private val PASSWORD                    = "password"
  final private val DRIVER_CLASS_NAME           = "driver_class_name"
  final private val TRANSACTION_ISOLATION       = "transaction_isolation"

  private val maxCore: Int = Runtime.getRuntime.availableProcessors()

  private def getCatalog: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](CATALOG))

  private def getConnectionTimeout: DatabaseCF[Option[Duration]] =
    readConfig(_.get[Option[Duration]](CONNECTION_TIMEOUT))

  private def getIdleTimeout: DatabaseCF[Option[Duration]] =
    readConfig(_.get[Option[Duration]](IDLE_TIMEOUT))

  private def getLeakDetectionThreshold: DatabaseCF[Option[Duration]] =
    readConfig(_.get[Option[Duration]](LEAK_DETECTION_THRESHOLD))

  private def getMaximumPoolSize: DatabaseCF[Option[Int]] =
    readConfig(_.get[Option[Int]](MAXIMUM_POOL_SIZE))

  private def getMaxLifetime: DatabaseCF[Option[Duration]] =
    readConfig(_.get[Option[Duration]](MAX_LIFETIME))

  private def getMinimumIdle: DatabaseCF[Option[Int]] =
    readConfig(_.get[Option[Int]](MINIMUM_IDLE))

  private def getPoolName: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](POOL_NAME))

  private def getValidationTimeout: DatabaseCF[Option[Duration]] =
    readConfig(_.get[Option[Duration]](VALIDATION_TIMEOUT))

  private def getAllowPoolSuspension: DatabaseCF[Option[Boolean]] =
    readConfig(_.get[Option[Boolean]](ALLOW_POOL_SUSPENSION))

  private def getAutoCommit: DatabaseCF[Option[Boolean]] =
    readConfig(_.get[Option[Boolean]](AUTO_COMMIT))

  private def getConnectionInitSql: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](CONNECTION_INIT_SQL))

  private def getConnectionTestQuery: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](CONNECTION_TEST_QUERY))

  private def getDataSourceClassname: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](DATA_SOURCE_CLASSNAME))

  private def getDatasourceJndi: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](DATASOURCE_JNDI))

  private def getInitializationFailTimeout: DatabaseCF[Option[Duration]] =
    readConfig(_.get[Option[Duration]](INITIALIZATION_FAIL_TIMEOUT))

  private def getIsolateInternalQueries: DatabaseCF[Option[Boolean]] =
    readConfig(_.get[Option[Boolean]](ISOLATE_INTERNAL_QUERIES))

  private def getJdbcUrl: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](JDBC_URL))

  private def getReadonly: DatabaseCF[Option[Boolean]] =
    readConfig(_.get[Option[Boolean]](READONLY))

  private def getRegisterMbeans: DatabaseCF[Option[Boolean]] =
    readConfig(_.get[Option[Boolean]](REGISTER_MBEANS))

  private def getSchema: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](SCHEMA))

  protected def getUserName: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](USERNAME))

  protected def getPassWord: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](PASSWORD))

  protected def getDriverClassName: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](DRIVER_CLASS_NAME))

  protected def getTransactionIsolation: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](TRANSACTION_ISOLATION)).map { v =>
      if v == "TRANSACTION_NONE" || v == "TRANSACTION_READ_UNCOMMITTED" || v == "TRANSACTION_READ_COMMITTED" || v == "TRANSACTION_REPEATABLE_READ" || v == "TRANSACTION_SERIALIZABLE"
      then v
      else
        throw new IllegalArgumentException(
          "TransactionIsolation must be TRANSACTION_NONE,TRANSACTION_READ_UNCOMMITTED,TRANSACTION_READ_COMMITTED,TRANSACTION_REPEATABLE_READ,TRANSACTION_SERIALIZABLE."
        )
    }

  final protected def readConfig[T](func: Configuration => Option[T]): DatabaseCF[Option[T]] =
    val dataSource = summon[DatabaseConfig]
    Seq(
      dataSource.path + "." + dataSource.database + "." + dataSource.hostspec,
      dataSource.path + "." + dataSource.database,
      dataSource.path + "." + dataSource.hostspec,
      dataSource.path
    ).foldLeft[Option[T]](None) {
      case (prev, path) =>
        prev.orElse {
          config.get[Option[Configuration]](path).flatMap(func(_))
        }
    }

  val connectionTimeout:      DatabaseCF[Long] = getConnectionTimeout.getOrElse(Duration(30, TimeUnit.SECONDS)).toMillis
  val idleTimeout:            DatabaseCF[Long] = getIdleTimeout.getOrElse(Duration(10, TimeUnit.MINUTES)).toMillis
  val leakDetectionThreshold: DatabaseCF[Long] = getLeakDetectionThreshold.getOrElse(Duration.Zero).toMillis
  val maximumPoolSize:        DatabaseCF[Int]  = getMaximumPoolSize.getOrElse(maxCore * 2)
  val maxLifetime:            DatabaseCF[Long] = getMaxLifetime.getOrElse(Duration(30, TimeUnit.MINUTES)).toMillis
  val minimumIdle:            DatabaseCF[Int]  = getMinimumIdle.getOrElse(10)
  val validationTimeout:      DatabaseCF[Long] = getValidationTimeout.getOrElse(Duration(5, TimeUnit.SECONDS)).toMillis
  val allowPoolSuspension: DatabaseCF[Boolean] = getAllowPoolSuspension.getOrElse(false)
  val autoCommit:          DatabaseCF[Boolean] = getAutoCommit.getOrElse(true)
  val initializationFailTimeout: DatabaseCF[Long] =
    getInitializationFailTimeout.getOrElse(Duration(1, TimeUnit.MILLISECONDS)).toMillis
  val isolateInternalQueries: DatabaseCF[Boolean] = getIsolateInternalQueries.getOrElse(false)
  val readonly:               DatabaseCF[Boolean] = getReadonly.getOrElse(false)
  val registerMbeans:         DatabaseCF[Boolean] = getRegisterMbeans.getOrElse(false)

  def makeFromDatabaseConfig(
    dataSource:            Option[DataSource] = None,
    dataSourceProperties:  Option[Properties] = None,
    healthCheckProperties: Option[Properties] = None,
    healthCheckRegistry:   Option[Object] = None,
    metricRegistry:        Option[Object] = None,
    metricsTrackerFactory: Option[MetricsTrackerFactory] = None,
    scheduledExecutor:     Option[ScheduledExecutorService] = None,
    threadFactory:         Option[ThreadFactory] = None
  ): DatabaseCF[HikariConfig] =
    val hikariConfig = new HikariConfig()

    getCatalog foreach hikariConfig.setCatalog
    hikariConfig.setConnectionTimeout(connectionTimeout)
    hikariConfig.setIdleTimeout(idleTimeout)
    hikariConfig.setMaximumPoolSize(maximumPoolSize)
    hikariConfig.setMaxLifetime(maxLifetime)
    hikariConfig.setMinimumIdle(minimumIdle)
    hikariConfig.setValidationTimeout(validationTimeout)
    hikariConfig.setAllowPoolSuspension(allowPoolSuspension)
    hikariConfig.setAutoCommit(autoCommit)
    hikariConfig.setInitializationFailTimeout(initializationFailTimeout)
    hikariConfig.setIsolateInternalQueries(isolateInternalQueries)
    hikariConfig.setReadOnly(readonly)
    hikariConfig.setRegisterMbeans(registerMbeans)

    getPassWord foreach hikariConfig.setPassword
    getPoolName foreach hikariConfig.setPoolName
    getUserName foreach hikariConfig.setUsername
    getConnectionInitSql foreach hikariConfig.setConnectionInitSql
    getConnectionTestQuery foreach hikariConfig.setConnectionTestQuery
    getDataSourceClassname foreach hikariConfig.setDataSourceClassName
    getDatasourceJndi foreach hikariConfig.setDataSourceJNDI
    getDriverClassName foreach hikariConfig.setDriverClassName
    getJdbcUrl foreach hikariConfig.setJdbcUrl
    getSchema foreach hikariConfig.setSchema
    getTransactionIsolation foreach hikariConfig.setTransactionIsolation

    dataSource foreach hikariConfig.setDataSource
    dataSourceProperties foreach hikariConfig.setDataSourceProperties
    healthCheckProperties foreach hikariConfig.setHealthCheckProperties
    healthCheckRegistry foreach hikariConfig.setHealthCheckRegistry
    metricRegistry foreach hikariConfig.setMetricRegistry
    metricsTrackerFactory foreach hikariConfig.setMetricsTrackerFactory
    scheduledExecutor foreach hikariConfig.setScheduledExecutor
    threadFactory foreach hikariConfig.setThreadFactory

    hikariConfig
