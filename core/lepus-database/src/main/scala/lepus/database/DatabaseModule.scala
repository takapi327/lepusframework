/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.database

/**
 * Module for implicitly passing the Transactor generated for each Database to DoobieRepository.
 *
 * @tparam F
 *   the effect type.
 *
 * example:
 * {{{
 *   case class ToDoDatabase(
 *     database: DatabaseConfig
 *   )(using DBTransactor[F]) extends DatabaseModule[F]:
 *
 *     val taskRepository     = new TaskRepository
 *     val categoryRepository = new CategoryRepository
 * }}}
 */
trait DatabaseModule[F[_]](using dbt: DBTransactor[F]):

  def database: DatabaseConfig

  private val transactor: Transactor[F] = dbt
    .get(database)
    .orElse(
      dbt
        .flatMap((db, xa) => {
          if db equals database then Some(xa) else None
        })
        .headOption
    ).getOrElse(throw new IllegalStateException(s"$database database is not registered."))

  given Transactor[F] = transactor
