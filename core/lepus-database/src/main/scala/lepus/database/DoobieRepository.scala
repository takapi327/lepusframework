/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import cats.data.{ OptionT, EitherT, Kleisli }

import cats.effect.Async
import cats.effect.kernel.MonadCancelThrow

import fs2.{ Pipe, Stream }

/** Trait to build Repository for DB connection using doobie.
  *
  * @tparam F
  *   the effect type.
  *
  * example:
  * {{{
  *   import cats.effect.IO
  *
  *   import doobie.Transactor
  *   import doobie.implicits.*
  *
  *   class TaskRepository(using: Transactor[IO]) extends DoobieRepository[IO]:
  *
  *     def getAll(): IO[List[Task]] =
  *       sql"SELECT * FROM task".query[Task].to[List]
  * }}}
  */
trait DoobieRepository[F[_]: Async: MonadCancelThrow](using xa: Transactor[F]) extends DoobieLogHandler:

  given [T]: Conversion[ConnectionIO[T], F[T]] with
    override def apply(connection: ConnectionIO[T]): F[T] =
      connection.transact(xa)

  given [T]: Conversion[OptionT[ConnectionIO, T], OptionT[F, T]] with
    override def apply(connection: OptionT[ConnectionIO, T]): OptionT[F, T] =
      connection.transact(xa)

  given [T, E]: Conversion[EitherT[ConnectionIO, E, T], EitherT[F, E, T]] with
    override def apply(connection: EitherT[ConnectionIO, E, T]): EitherT[F, E, T] =
      connection.transact(xa)

  given [T, E]: Conversion[Kleisli[ConnectionIO, E, T], Kleisli[F, E, T]] with
    override def apply(connection: Kleisli[ConnectionIO, E, T]): Kleisli[F, E, T] =
      connection.transact(xa)

  given [T]: Conversion[Stream[ConnectionIO, T], Stream[F, T]] with
    override def apply(connection: Stream[ConnectionIO, T]): Stream[F, T] =
      connection.transact(xa)

  given [A, B]: Conversion[Pipe[ConnectionIO, A, B], Pipe[F, A, B]] with
    override def apply(connection: Pipe[ConnectionIO, A, B]): Pipe[F, A, B] =
      connection.transact(xa)

  given streamToKleisli[A, B]
    : Conversion[Stream[[T] =>> Kleisli[ConnectionIO, A, T], B], Stream[[T] =>> Kleisli[F, A, T], B]] with
    override def apply(
      connection: Stream[[T] =>> Kleisli[ConnectionIO, A, T], B]
    ): Stream[[T] =>> Kleisli[F, A, T], B] =
      connection.transact(xa)
