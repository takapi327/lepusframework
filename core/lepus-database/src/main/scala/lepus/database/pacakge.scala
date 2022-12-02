/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import javax.sql.DataSource as JDataSource

package object database:
  type DatabaseCF[T] = DatabaseConfig ?=> T
