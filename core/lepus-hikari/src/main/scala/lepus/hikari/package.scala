/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import com.zaxxer.hikari.HikariDataSource

import lepus.database.DatabaseContext

package object hikari:
  type HikariContext = DatabaseContext[HikariDataSource]
