/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database.specs2

import scala.annotation.targetName

import cats.data.{ OptionT, EitherT, Kleisli }

import cats.effect.Async
import cats.effect.kernel.MonadCancelThrow

import fs2.{ Pipe, Stream }

import lepus.database.*
import lepus.database.implicits.*

/** Test by actually accessing the database.
  *
  * Note that any SQL executed during testing will be rolled back.
  *
  * @tparam F
  *   the effect type.
  *
  * example:
  * {{{
  *   class TestRepositoryDBAccessTest extends Specification, DBAccessSpecification[IO]:
  *
  *     val database: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo", NonEmptyList.of("master", "slave"))
  *
  *     "TestRepository Test" should {
  *       "Check findAll database access" in {
  *         val result = TestRepository.findAll().rollbackTransact("slave").unsafeRunSync()
  *         result.length === 3
  *       }
  *
  *       "Check update database access" in {
  *         val task = Task(Some(1), "Task1 Updated", None, Task.Status.TODO)
  *         val result = TestRepository.update(task).rollbackTransact("slave").unsafeRunSync()
  *         result === 1
  *       }
  *     }
  * }}}
  */
trait DBAccessSpecification[F[_]: Async: MonadCancelThrow] extends DriverBuilder:

  /** Value with configuration to establish a connection to Database */
  def database: DatabaseConfig

  /** Methods for retrieving connections to the specified database.
    *
    * Note that this connection is for testing and all executions will be rolled back.
    */
  private lazy val rollbackTransactor: String => Transactor[F] = (key: String) =>
    Transactor.after.set(
      database.dataSource
        .find(_.replication == key)
        .map(makeFromDataSource[F](_))
        .getOrElse(throw new IllegalArgumentException(s"$key is not set as replication for the specified $database.")),
      HC.rollback
    )

  extension [T](connection: ConnectionIO[T])
    def rollbackTransact(key: String): F[T] =
      connection.transact(rollbackTransactor(key))

  extension [T](connection: OptionT[ConnectionIO, T])
    def rollbackTransact(key: String): OptionT[F, T] =
      connection.transact(rollbackTransactor(key))

  extension [T, E](connection: EitherT[ConnectionIO, E, T])
    def rollbackTransact(key: String): EitherT[F, E, T] =
      connection.transact(rollbackTransactor(key))

  extension [T, E](connection: Kleisli[ConnectionIO, E, T])
    def rollbackTransact(key: String): Kleisli[F, E, T] =
      connection.transact(rollbackTransactor(key))

  extension [T](connection: Stream[ConnectionIO, T])
    def rollbackTransact(key: String): Stream[F, T] =
      connection.transact(rollbackTransactor(key))

  extension [A, B](connection: Pipe[ConnectionIO, A, B])
    def rollbackTransact(key: String): Pipe[F, A, B] =
      connection.transact(rollbackTransactor(key))

  extension [A, B](connection: Stream[[T] =>> Kleisli[ConnectionIO, A, T], B])
    @targetName("streamToKleisliRollbackTransact")
    def rollbackTransact(key: String): Stream[[T] =>> Kleisli[F, A, T], B] =
      connection.transact(rollbackTransactor(key))
