/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie.specs2

import cats.effect.Async

import lepus.database.*

import lepus.doobie.*

/** Trait for probing temporary DB connection for testing
  */
trait DriverBuilder extends DataSourceConfigReader:

  final private val JDBC_URL          = "jdbc_url"
  final private val USERNAME          = "username"
  final private val PASSWORD          = "password"
  final private val DRIVER_CLASS_NAME = "driver_class_name"

  /** Method to retrieve jdbc url information from the conf file. */
  private def getJdbcUrl: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](JDBC_URL))

  /** Method to retrieve user name information from the conf file. */
  protected def getUserName: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](USERNAME))

  /** Method to retrieve password information from the conf file. */
  protected def getPassWord: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](PASSWORD))

  /** Method to retrieve driver class name information from the conf file. */
  protected def getDriverClassName: DatabaseCF[Option[String]] =
    readConfig(_.get[Option[String]](DRIVER_CLASS_NAME))

  def makeFromDataSource[F[_]: Async](dataSource: DataSource): Transactor.Aux[F, Unit] =
    given DataSource = dataSource
    (for
      driver <- getDriverClassName
      url    <- getJdbcUrl
      user   <- getUserName
    yield Transactor.fromDriverManager[F](
      driver,
      url,
      user,
      getPassWord.getOrElse("")
    )).getOrElse(throw new IllegalArgumentException("The values for driver, url, and user may be incorrect or not set"))
