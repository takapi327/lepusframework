/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import doobie.Transactor

package object database extends LepusDoobie:
  type DatabaseCF[T] = DatabaseConfig ?=> T

  type DBTransactor[F[_]] = Map[DatabaseConfig, Transactor[F]]
