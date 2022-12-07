/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import com.google.inject.Injector

import cats.data.NonEmptyList

import org.http4s.server.middleware.CORSPolicy

import lepus.app.LepusApp

/** A model for providing server configuration information. Only one must be generated by the application.
 *
 * For example:
 * {{{
 *  object HttpApp extends LepusRouter[IO]:
 *    val routes = NonEmptyList.of(
 *      "hello" / name ->> RouterConstructor.of {
 *        case GET => Ok(s"Hello $name")
 *      }
 *    )
 * }}}
 *
 * @tparam F
 *   the effect type.
 */
trait LepusRouter[F[_]] extends LepusApp[F]:

  /** CORS settings applied to all endpoints */
  val cors: Option[CORSPolicy] = None

  /** List of all endpoints to be launched by the application */
  val routes: Injector ?=> NonEmptyList[Routing[F]]
