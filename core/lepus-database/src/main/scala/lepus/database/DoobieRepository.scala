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

  private val connection: Option[Transactor[F]] = summon[DBTransactor[F]]
    .flatMap((db, xa) => {
      if db equals database then Some(xa) else None
    })
    .headOption

  object Action:
    @targetName("default") def apply[T](func: Transactor[F] => F[T]): F[T] =
      connection match
        case Some(transactor) => func(transactor)
        case None             => throw new IllegalStateException(s"$database database is not registered.")
    @targetName("defaultK") def apply[K[_], T](func: Transactor[F] => K[T]): K[T] =
      connection match
        case Some(transactor) => func(transactor)
        case None             => throw new IllegalStateException(s"$database database is not registered.")

    def transact[T](func: ConnectionIO[T]): F[T] =
      this.apply(func.transact[F])
    def transact[T](func: OptionT[ConnectionIO, T]): OptionT[F, T] =
      this.apply[[A] =>> OptionT[F, A], T](func.transact[F])
    def transact[T, E](func: EitherT[ConnectionIO, E, T]): EitherT[F, E, T] =
      this.apply[[A] =>> EitherT[F, E, A], T](func.transact[F])
    def transact[T, E](func: Kleisli[ConnectionIO, E, T]): Kleisli[F, E, T] =
      this.apply[[A] =>> Kleisli[F, E, A], T](func.transact[F])
    def transact[T](func: Stream[F, T])(using Stream[F, T] =:= Stream[ConnectionIO, T]): Stream[F, T] =
      this.apply[[A] =>> Stream[F, A], T](func.transact[F])
    def transact[A, B](func: Pipe[F, A, B])(using Pipe[F, A, B] =:= Pipe[ConnectionIO, A, B]): Pipe[F, A, B] =
      this.apply[[T] =>> Pipe[F, T, B], A](func.transact[F])
    def transact[A, B](func: Stream[[T] =>> Kleisli[ConnectionIO, A, T], B]): Stream[[T] =>> Kleisli[F, A, T], B] =
      connection match
        case Some(transactor) => func.transact[F](transactor)
        case None             => throw new IllegalStateException(s"$database database is not registered.")
