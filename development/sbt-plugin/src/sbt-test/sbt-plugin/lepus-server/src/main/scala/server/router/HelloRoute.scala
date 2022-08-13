/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.effect.IO

import lepus.router.{ *, given }
import lepus.router.model.ServerResponse

object HelloRoute extends RouterConstructor[IO, String]:

  override def routes = {
    case GET => IO(ServerResponse.NoContent)
  }
