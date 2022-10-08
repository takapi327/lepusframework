/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import scala.annotation.targetName

import cats.data.{ OptionT, EitherT, Kleisli }

import cats.effect.Async

import fs2.{ Pipe, Stream }

import doobie.Transactor
import doobie.util.log.LogHandler
import doobie.util.fragment.Fragment

trait DoobieRepository[F[_]: Async](using DBTransactor[F]) extends DoobieLogHandler:

  given LogHandler = logHandler

  def database: DatabaseConfig
  def table: String

  private val connection: Option[Transactor[F]] = summon[DBTransactor[F]]
    .flatMap((db, xa) => {
      if db equals database then Some(xa) else None
    })
    .headOption

  def select(params: String*): Fragment =
    fr"SELECT" ++ Fragment.const(params.mkString(",")) ++ fr"FROM" ++ Fragment.const(table)

  object Action:
    @targetName("default") def apply[T](func: Transactor[F] => F[T]): F[T] =
      connection match
        case Some(transactor) => func(transactor)
        case None             => throw new IllegalStateException(s"$database database is not registered.")
    @targetName("defaultK") def apply[K[_], T](func: Transactor[F] => K[T]): K[T] =
      connection match
        case Some(transactor) => func(transactor)
        case None             => throw new IllegalStateException(s"$database database is not registered.")
