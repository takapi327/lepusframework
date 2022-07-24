/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.effect._

import io.circe._
import io.circe.generic.semiauto._

import lepus.router._
import lepus.router.http._
import lepus.router.model.Schema
import lepus.router.generic.semiauto._
import lepus.router.model.ServerResponse

case class Sample(info: String)
object Sample {
  implicit val encoder: Encoder[Sample] = deriveEncoder
  implicit val schema:  Schema[Sample]  = deriveSchemer
}

object HelloRoute extends RouterConstructor[IO, String] {

  override def endpoint = "hello" / bindPath[String]("name")

  override def summary     = Some("Sample Paths")
  override def description = Some("Sample Paths")

  override def responses: PartialFunction[RequestMethod, List[Response[_]]] = {
    case GET => List(
      Response.build[Sample](
        status      = responseStatus.Ok,
        headers     = List.empty,
        description = "Sample information acquisition"
      )
    )
  }

  override def routes = {
    case GET => req => IO(ServerResponse.NoContent)
  }
}
