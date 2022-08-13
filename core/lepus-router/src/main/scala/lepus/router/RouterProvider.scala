/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import cats.data.NonEmptyList

import lepus.router.http.Request

/** A model for providing routing information to the server. Only one must be generated by the application.
  *
  * For example:
  * {{{
  *  object HttpApp extends RouterProvider[IO]:
  *    override def routes = combine(
  *      "hello" / name -> HelloRoute,
  *      "world" / country -> WorldRoute
  *    )
  * }}}
  *
  * @tparam F
  *   the effect type.
  */
trait RouterProvider[F[_]]:

  def routes: NonEmptyList[Routing[F]]
