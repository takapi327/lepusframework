/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.effect.IO

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.server.Router

import lepus.app.LepusApp

object HttpApp extends LepusApp[IO]:

  val router = Router(
    "/" -> HttpRoutes.of[IO] {
      case GET -> Root / "hello" / name => Ok(s"Hello $name")
    }
  )
