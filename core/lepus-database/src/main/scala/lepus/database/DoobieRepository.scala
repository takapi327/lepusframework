/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.database

import cats.effect.Async

import doobie.Transactor
import doobie.util.log.LogHandler

trait DoobieRepository[F[_] : Async](using DBTransactor[F]) extends DoobieLogHandler :

  given LogHandler = logHandler

  def database: DatabaseConfig

  private val connection: Option[Transactor[F]] = summon[DBTransactor[F]].flatMap((db, xa) => {
    if db equals database then Some(xa) else None
  }).headOption

  object Action:
    def apply[T](func: Transactor[F] => F[T]): F[T] =
      connection match
        case Some(transactor) => func(transactor)
        case None => throw new IllegalStateException(s"$database database is not registered.")
