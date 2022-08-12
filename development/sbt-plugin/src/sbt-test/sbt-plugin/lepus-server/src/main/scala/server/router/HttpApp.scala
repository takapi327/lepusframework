/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.effect.IO

import lepus.router.{ *, given }
import lepus.router.model.{ Schema, ServerResponse }
import lepus.router.http.Method
import lepus.router.generic.semiauto.*

case class Hello(name: String)
object Hello:
  given Schema[Hello] = deriveSchemer

case class World(name: String)
object World:
  given Schema[World] = deriveSchemer

object HelloWorldController:
  def get = IO(ServerResponse.NoContent)

object HttpApp extends RouterProvider[IO]:

  override def routes = combine {
    "hello" / bindPath[String]("name") -> Router.of[IO, Hello, World, String] {
      case Method.Get => HelloWorldController.get
    }
  }
