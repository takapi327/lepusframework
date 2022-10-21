/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import scala.annotation.targetName

import cats.data.{ OptionT, EitherT, Kleisli }

import cats.effect.Async
import cats.effect.kernel.MonadCancelThrow

import fs2.{ Pipe, Stream }

import lepus.database.implicits.*

/** Trait to build Repository for DB connection using doobie.
  *
  * @tparam F
  *   the effect type.
  *
  * example:
  * {{{
  *   import cats.effect.IO
  *
  *   import lepus.database.implicits.*
  *
  *   case class TaskRepository()(using TodoDatabase) extends DoobieRepository[IO, TodoDatabase]:
  *
  *     def getAll(): IO[List[Task]] = RunDB {
  *       sql"SELECT * FROM task".query[Task].to[List]
  *     }
  *
  * }}}
  */
trait DoobieRepository[F[_]: Async: MonadCancelThrow, D <: DatabaseModule[F]](using
  database: D
) extends DoobieLogHandler:

  given [T](using xa: Transactor[F]): Conversion[ConnectionIO[T], F[T]] =
    connection => connection.transact(xa)

  given [T](using xa: Transactor[F]): Conversion[OptionT[ConnectionIO, T], OptionT[F, T]] = connection =>
    connection.transact(xa)

  given [T, E](using xa: Transactor[F]): Conversion[EitherT[ConnectionIO, E, T], EitherT[F, E, T]] = connection =>
    connection.transact(xa)

  given [T, E](using xa: Transactor[F]): Conversion[Kleisli[ConnectionIO, E, T], Kleisli[F, E, T]] = connection =>
    connection.transact(xa)

  given [T](using xa: Transactor[F]): Conversion[Stream[ConnectionIO, T], Stream[F, T]] = connection =>
    connection.transact(xa)

  given [A, B](using xa: Transactor[F]): Conversion[Pipe[ConnectionIO, A, B], Pipe[F, A, B]] = connection =>
    connection.transact(xa)

  given streamToKleisli[A, B](using
    xa: Transactor[F]
  ): Conversion[Stream[[T] =>> Kleisli[ConnectionIO, A, T], B], Stream[[T] =>> Kleisli[F, A, T], B]] = connection =>
    connection.transact(xa)

  object RunDB:

    @targetName("default") def apply[T](func: Transactor[F] => F[T]): F[T] =
      func(database.transactor(database.defaultDB))
    @targetName("defaultK") def apply[K[_], T](func: Transactor[F] => K[T]): K[T] =
      func(database.transactor(database.defaultDB))
    @targetName("apply") def apply[T](key: String)(func: Transactor[F] => F[T]): F[T] =
      func(database.transactor(key))
    @targetName("applyK") def apply[K[_], T](key: String)(func: Transactor[F] => K[T]): K[T] =
      func(database.transactor(key))
    def apply[T](key: String)(func: ConnectionIO[T]): F[T] =
      given Transactor[F] = database.transactor(key)
      func
    def apply[T](key: String)(func: OptionT[ConnectionIO, T]): OptionT[F, T] =
      given Transactor[F] = database.transactor(key)
      func
    def apply[T, E](key: String)(func: EitherT[ConnectionIO, E, T]): EitherT[F, E, T] =
      given Transactor[F] = database.transactor(key)
      func
    def apply[T, E](key: String)(func: Kleisli[ConnectionIO, E, T]): Kleisli[F, E, T] =
      given Transactor[F] = database.transactor(key)
      func
    def apply[T](key: String)(func: Stream[F, T]): Stream[F, T] =
      given Transactor[F] = database.transactor(key)
      func
    def apply[A, B](key: String)(func: Pipe[F, A, B]): Pipe[F, A, B] =
      given Transactor[F] = database.transactor(key)
      func
    @targetName("streamToKleisli") def apply[A, B](key: String)(
      func:                                             Stream[[T] =>> Kleisli[ConnectionIO, A, T], B]
    ): Stream[[T] =>> Kleisli[F, A, T], B] =
      given Transactor[F] = database.transactor(key)
      func
    def apply[T](func: ConnectionIO[T]): F[T] =
      given Transactor[F] = database.transactor(database.defaultDB)
      func
    def apply[T](func: OptionT[ConnectionIO, T]): OptionT[F, T] =
      given Transactor[F] = database.transactor(database.defaultDB)
      func
    def apply[T, E](func: EitherT[ConnectionIO, E, T]): EitherT[F, E, T] =
      given Transactor[F] = database.transactor(database.defaultDB)
      func
    def apply[T, E](func: Kleisli[ConnectionIO, E, T]): Kleisli[F, E, T] =
      given Transactor[F] = database.transactor(database.defaultDB)
      func
    def apply[T](func: Stream[F, T]): Stream[F, T] =
      given Transactor[F] = database.transactor(database.defaultDB)
      func
    def apply[A, B](func: Pipe[F, A, B]): Pipe[F, A, B] =
      given Transactor[F] = database.transactor(database.defaultDB)
      func
    @targetName("streamToKleisli") def apply[A, B](
      func: Stream[[T] =>> Kleisli[ConnectionIO, A, T], B]
    ): Stream[[T] =>> Kleisli[F, A, T], B] =
      given Transactor[F] = database.transactor(database.defaultDB)
      func
