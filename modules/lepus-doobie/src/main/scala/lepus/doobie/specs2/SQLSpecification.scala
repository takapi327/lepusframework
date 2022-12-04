/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie.specs2

import cats.effect.IO

import org.specs2.mutable.Specification

import lepus.database.DatabaseConfig

import lepus.doobie.*

/** Trait for testing SQL query statement checks.
  *
  * example:
  * {{{
  *   class SQLTest extends SQLSpecification:
  *
  *     def databaseConfig: DatabaseConfig = DatabaseConfig("lepus.database://query_test/writer")
  *
  *     "TestRepository Test" should {
  *       "Check sql query format" in {
  *         check(sql"SELECT id, title, description, state FROM todo_task".query[Task])
  *       }
  *     }
  * }}}
  */
trait SQLSpecification extends Specification, DriverBuilder, IOChecker:

  /** DatabaseConfig to build the database you want to test */
  def databaseConfig: DatabaseConfig

  /** Connect to the database you wish to test. The purpose is to perform a test run, so all processing is rolled back.
    */
  val transactor: Transactor[IO] =
    Transactor.after.set(makeFromDatabaseConfig[IO](databaseConfig), HC.rollback)
