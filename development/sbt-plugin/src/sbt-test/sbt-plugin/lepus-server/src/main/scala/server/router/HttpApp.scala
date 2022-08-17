/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.data.NonEmptyList

import cats.effect.IO

import lepus.router.{ *, given }
import lepus.router.http.Response

object HttpApp extends RouterProvider[IO]:

  override def routes = NonEmptyList.of(
    "hello" / bindPath[String]("name") -> RouterConstructor.of {
      case GET => IO(Response.NoContent)
    }
  )
