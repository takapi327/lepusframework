/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import scala.annotation.targetName

import cats.data.{ OptionT, EitherT, Kleisli }

import cats.effect.IO
import cats.effect.kernel.MonadCancelThrow

import fs2.{ Pipe, Stream }

/** Aliases for top-level imports defined in doobie
  */
trait LepusDoobie extends doobie.Aliases, doobie.hi.Modules, doobie.free.Modules, doobie.free.Types:

  object implicits
    extends doobie.free.Instances,
            doobie.syntax.AllSyntax,
            doobie.util.meta.SqlMeta,
            doobie.util.meta.TimeMeta,
            doobie.util.meta.LegacyMeta:

    extension [T](connection: ConnectionIO[T])(using db: DatabaseModule[IO])
      def transaction: IO[T] =
        connection.transact(db.transactor(db.defaultDB))
      def transaction(key: String): IO[T] =
        connection.transact(db.transactor(key))

    extension [T](connection: OptionT[ConnectionIO, T])(using db: DatabaseModule[IO])
      def transaction: OptionT[IO, T] =
        connection.transact(db.transactor(db.defaultDB))
      def transaction(key: String): OptionT[IO, T] =
        connection.transact(db.transactor(key))

    extension [T, E](connection: EitherT[ConnectionIO, E, T])(using db: DatabaseModule[IO])
      def transaction: EitherT[IO, E, T] =
        connection.transact(db.transactor(db.defaultDB))
      def transaction(key: String): EitherT[IO, E, T] =
        connection.transact(db.transactor(key))

    extension [T, E](connection: Kleisli[ConnectionIO, E, T])(using db: DatabaseModule[IO])
      def transaction: Kleisli[IO, E, T] =
        connection.transact(db.transactor(db.defaultDB))
      def transaction(key: String): Kleisli[IO, E, T] =
        connection.transact(db.transactor(key))

    extension [T](connection: Stream[ConnectionIO, T])(using db: DatabaseModule[IO])
      def transaction: Stream[IO, T] =
        connection.transact(db.transactor(db.defaultDB))
      def transaction(key: String): Stream[IO, T] =
        connection.transact(db.transactor(key))

    extension [A, B](connection: Pipe[ConnectionIO, A, B])(using db: DatabaseModule[IO])
      def transaction: Pipe[IO, A, B] =
        connection.transact(db.transactor(db.defaultDB))
      def transaction(key: String): Pipe[IO, A, B] =
        connection.transact(db.transactor(key))

    extension [A, B](connection: Stream[[T] =>> Kleisli[ConnectionIO, A, T], B])(using db: DatabaseModule[IO])
      @targetName("streamToKleisliDefaultTransaction")
      def transaction: Stream[[T] =>> Kleisli[IO, A, T], B] =
        connection.transact(db.transactor(db.defaultDB))
      @targetName("streamToKleisliTransaction")
      def transaction(key: String): Stream[[T] =>> Kleisli[IO, A, T], B] =
        connection.transact(db.transactor(key))
