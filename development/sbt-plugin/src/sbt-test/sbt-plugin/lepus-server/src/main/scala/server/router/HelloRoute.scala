/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.effect._

import lepus.router._
import lepus.router.model.ServerResponse

object HelloRoute extends RouterConstructor[IO, String] {

  override def endpoint = "hello" / bindPath[String]("name")

  override def routes = {
    case GET => req => IO(ServerResponse.NoContent)
  }
}
