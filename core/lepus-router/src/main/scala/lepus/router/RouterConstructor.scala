/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

/** A model that contains one routing information.
  *
  * For example:
  * {{{
  *   // http:localhost:5555/hello/world/lepus
  *   object HelloRoute extends RouterConstructor[IO, String]:
  *     def routes = {
  *       case GET => IO(ServerResponse.NoContent)
  *     }
  *
  *   // There is also a way to use it without objects.
  *   "hello" / bindPath[String]("world") -> RouterConstructor.of {
  *     case GET => HelloWorldController.get
  *   }
  * }}}
  *
  * @tparam F
  *   the effect type.
  * @tparam T
  *   Endpoint Type
  */
trait RouterConstructor[F[_], T]:
  /** Corresponding logic for each method of this endpoint. */
  def routes: Requestable[F][T]

object RouterConstructor:
  def of[F[_], T](
    requestable: Requestable[F][T]
  ): RouterConstructor[F, T] = new RouterConstructor[F, T]:
    override def routes: Requestable[F][T] = requestable
