/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import javax.sql.DataSource as JDataSource

package object database:
  type DatabaseCF[T]                  = DataSource ?=> T
  type LepusContext[T <: JDataSource] = Map[DataSource, DatabaseContext[T]]

  def emptyContext[T <: JDataSource] = Map.empty[DataSource, DatabaseContext[T]]
