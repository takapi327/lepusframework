/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.database

import javax.sql.DataSource

import scala.concurrent.ExecutionContext

/**
 * Model with threads and DataSource for database connection
 *
 * @param ec
 *   ExecutionContext generated for database connection
 * @param ds
 *   JDBC DataSource
 */
case class DatabaseContext[T <: DataSource](
  ec: ExecutionContext,
  ds: T
)
