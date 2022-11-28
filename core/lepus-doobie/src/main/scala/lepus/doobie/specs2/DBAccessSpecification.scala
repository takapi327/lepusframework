/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie.specs2

import cats.effect.Async

import lepus.database.DatabaseConfig

import lepus.doobie.*

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
  *     val database: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo/writer"))
  *
  *     "TestRepository Test" should {
  *       "Check findAll database access" in {
  *         val result = TestRepository.findAll().transact(rollbackTransactor).unsafeRunSync()
  *         result.length === 3
  *       }
  *
  *       "Check update database access" in {
  *         val task = Task(Some(1), "Task1 Updated", None, Task.Status.TODO)
  *         val result = TestRepository.update(task).transact(rollbackTransactor).unsafeRunSync()
  *         result === 1
  *       }
  *     }
  * }}}
  */
trait DBAccessSpecification[F[_]: Async] extends DriverBuilder:

  /** Value with configuration to establish a connection to Database */
  def database: DatabaseConfig

  /** Methods for retrieving connections to the specified database.
    *
    * Note that this connection is for testing and all executions will be rolled back.
    */
  protected  lazy val rollbackTransactor: Transactor[F] =
    Transactor.after.set(
      makeFromDatabaseConfig[F](database),
      HC.rollback
    )
