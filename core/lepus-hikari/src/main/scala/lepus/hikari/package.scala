/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus

import scala.concurrent.ExecutionContext

import com.zaxxer.hikari.HikariDataSource

import lepus.database.DataSource

package object hikari:
  type LepusContext = Map[DataSource, (ExecutionContext, HikariDataSource)]
