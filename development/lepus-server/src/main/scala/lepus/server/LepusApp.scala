/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import cats.data.NonEmptyList

import org.http4s.server.middleware.CORSPolicy

import doobie.Transactor

import lepus.router.Routing
import lepus.database.{ DatabaseConfig, DBTransactor }

/** @tparam F
  *   the effect type.
  */
trait LepusApp[F[_]]:

  // ----- [ Database setups ] -----
  /** List of all databases to be launched by the application */
  val databases: Set[DatabaseConfig] = Set.empty

  // ----- [ Router Setups ] -----
  /** CORS settings applied to all endpoints */
  def cors: Option[CORSPolicy] = None

  /** List of all endpoints to be launched by the application */
  def routes: DBTransactor[F] ?=> NonEmptyList[Routing[F]]
