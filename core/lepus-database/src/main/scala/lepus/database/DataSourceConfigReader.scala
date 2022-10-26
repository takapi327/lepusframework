/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import lepus.core.util.Configuration

/** Trait provides an implementation to retrieve data from a Conf file based on DataSource settings.
  */
private[lepus] trait DataSourceConfigReader:

  protected val config: Configuration = Configuration.load()

  /** Method to retrieve values matching any key from the conf file from the DatabaseConfig configuration, with any
    * type.
    *
    * @param func
    *   Process to get values from Configuration wrapped in Option
    * @tparam T
    *   Type of value retrieved from conf file
    */
  final protected def readConfig[T](func: Configuration => Option[T])(using dataSource: DataSource): Option[T] =
    Seq(
      dataSource.path + "." + dataSource.database + "." + dataSource.replication,
      dataSource.path + "." + dataSource.database,
      dataSource.path
    ).foldLeft[Option[T]](None) {
      case (prev, path) =>
        prev.orElse {
          config.get[Option[Configuration]](path).flatMap(func(_))
        }
    }
