/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.swagger

import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps

import lepus.router.model.Tag
import lepus.swagger.model._

/**
 *
 * @param openapi    OpenAPI version
 * @param info       API metadata
 * @param servers    Array of servers that provide the API
 * @param tags       Tags used to organize the API
 * @param paths      Paths and operations available as APIs
 * @param components An array of objects to use in the API
 */
final case class SwaggerUI(
  openapi:    String                         = "3.0.3",
  info:       Info,
  servers:    List[Server]                   = List.empty,
  tags:       Set[Tag]                       = Set.empty,
  paths:      Map[String, Map[String, Path]] = Map.empty,
  components: List[Component]                = List.empty
)

object SwaggerUI {

  implicit lazy val encodeTag: Encoder[Tag] = Encoder.instance {
    tag => Json.obj(
      "name"         -> tag.name.asJson,
      "description"  -> tag.description.asJson,
      "externalDocs" -> (for {
        desc <- tag.externalDocsDescription
        url  <- tag.externalDocsUrl
      } yield Json.obj(
        "description" -> desc.asJson,
        "url"         -> url.asJson
      )).asJson
    )
  }
  implicit lazy val encoder: Encoder[SwaggerUI] = deriveEncoder

  def build(info: Info, paths: Map[String, Map[String, Path]], tags: Set[Tag]): SwaggerUI =
    SwaggerUI(
      info  = info,
      paths = paths,
      tags  = tags
    )
}

/**
 *
 * @param title          API Title
 * @param version        The version of the API design document you are describing
 * @param description    Description of the API
 * @param termsOfService
 * @param contact
 * @param license
 */
final case class Info(
  title:          String,
  version:        String,
  description:    Option[String]  = None,
  termsOfService: Option[String]  = None,
  contact:        Option[Contact] = None,
  license:        Option[License] = None
)

object Info {
  implicit lazy val encoder: Encoder[Info] = deriveEncoder
}

final case class Contact(
  name:  Option[String] = None,
  email: Option[String] = None,
  url:   Option[String] = None
)

object Contact {
  implicit lazy val encoder: Encoder[Contact] = deriveEncoder
}

final case class License(
  name: String,
  url:  String
)

object License {
  implicit lazy val encoder: Encoder[License] = deriveEncoder
}

/**
 * Define the server that is serving the API.
 * For the development environment, define localhost, and for other environments, define staging, production, etc.
 *
 * @param url         The server address that provides the API.
 * @param description Additional information about the servers provided
 */
final case class Server(
  url:         String,
  description: Option[String] = None,
)

object Server {
  implicit lazy val encoder: Encoder[Server] = deriveEncoder
}

final case class Component()
object Component {
  implicit lazy val encoder: Encoder[Component] = deriveEncoder
}
