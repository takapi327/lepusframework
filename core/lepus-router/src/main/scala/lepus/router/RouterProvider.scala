/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import cats.data.NonEmptyList

import cats.effect.Async

import org.http4s.server.middleware.CORSPolicy

import lepus.router.http.Request

/** A model for providing routing information to the server. Only one must be generated by the application.
  *
  * For example:
  * {{{
  *  object HttpApp extends RouterProvider[IO]:
  *    override def routes = NonEmptyList.of(
  *      "hello" / name ->> HelloRoute,
  *      "world" / country ->> RouterConstructor.of {
  *        case GET => WorldController.get
  *      }
  *    )
  * }}}
  *
  * @tparam F
  *   the effect type.
  */
trait RouterProvider[F[_]](using Async[F]):

  /** CORS settings applied to all endpoints */
  def cors: Option[CORSPolicy] = None

  /** List of all endpoints to be launched by the application */
  def routes: NonEmptyList[Routing[F]]
