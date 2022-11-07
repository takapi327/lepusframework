/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.database.specs2

import cats.effect.IO

import org.specs2.mutable.Specification

import lepus.database.*

/**
 * Trait for testing SQL query statement checks.
 *
 * example:
 * {{{
 *   class SQLTest extends SQLSpecification:
 *
 *     def dataSource: DataSource = DataSource("lepus.database", "query_test", "master")
 *
 *     "TestRepository Test" should {
 *       "Check sql query format" in {
 *         check(sql"SELECT id, title, description, state FROM todo_task".query[Task])
 *       }
 *     }
 * }}}
 */
trait SQLSpecification extends Specification, DriverBuilder, IOChecker:

  /** DataSource to build the database you want to test */
  def dataSource: DataSource

  /**
   *  Connect to the database you wish to test.
   *  The purpose is to perform a test run, so all processing is rolled back.
   */
  val transactor: Transactor[IO] =
    Transactor.after.set(makeFromDataSource[IO](dataSource), HC.rollback)
