/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import cats.data.NonEmptyList

import lepus.router.model.Tag

/** A model for providing routing information to the server. Only one must be generated by the application.
  *
  * For example:
  * {{{
  *  object HttpApp extends RouterProvider[IO] {
  *    override def routes: NonEmptyList[RouterConstructor[IO]] =
  *      NonEmptyList.of(HelloRoute)
  *  }
  * }}}
  *
  * @tparam F
  *   the effect type.
  */
trait RouterProvider[F[_]] {

  /** Tag of this endpoint, used during Swagger (Open API) document generation. */
  def tags: Set[Tag] = Set.empty[Tag]

  def routes: NonEmptyList[RouterConstructor[F]]
}
