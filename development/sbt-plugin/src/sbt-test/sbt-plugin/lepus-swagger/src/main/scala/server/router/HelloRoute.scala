/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.effect.IO

import io.circe.*
import io.circe.generic.semiauto.*

import org.http4s.Method.*
import org.http4s.{ Response, Status }
import org.http4s.dsl.io.*

import lepus.router.{ *, given }
import lepus.router.http.*
import lepus.router.model.Schema
import lepus.router.generic.semiauto.*

import lepus.swagger.OpenApiConstructor
import lepus.swagger.model.OpenApiResponse

case class Sample(info: String)
object Sample:
  given Encoder[Sample] = deriveEncoder
  given Schema[Sample]  = deriveSchemer

object HelloRoute extends OpenApiConstructor[IO, String]:

  override def summary     = Some("Sample Paths")
  override def description = Some("Sample Paths")

  override def responses = {
    case GET => List(
      OpenApiResponse[Sample](Status.Ok, List.empty, "Sample information acquisition")
    )
  }

  override def routes = {
    case GET => Ok(s"hello ${summon[String]}")
  }
