/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.effect.IO

import io.circe.*
import io.circe.generic.semiauto.*

import lepus.router.{ *, given }
import lepus.router.http.*
import lepus.router.model.Schema
import lepus.router.generic.semiauto.*
import lepus.router.model.ServerResponse

import lepus.swagger.OpenApiConstructor

case class Sample(info: String)
object Sample:
  given Encoder[Sample] = deriveEncoder
  given Schema[Sample]  = deriveSchemer

object HelloRoute extends RouterConstructor[IO, String], OpenApiConstructor[IO, String]:

  override def endpoint = "hello" / bindPath[String]("name")

  override def summary     = Some("Sample Paths")
  override def description = Some("Sample Paths")

  override def responses = {
    case GET => List(
      Response.build[Sample](
        status      = status.Ok,
        headers     = List.empty,
        description = "Sample information acquisition"
      )
    )
  }

  override def routes = {
    case GET => req => IO(ServerResponse.NoContent)
  }
