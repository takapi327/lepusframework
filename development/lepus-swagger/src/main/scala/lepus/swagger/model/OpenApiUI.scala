/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import scala.collection.immutable.ListMap

import lepus.openapi.model.Tag

/** @param openapi
  *   OpenAPI version
  * @param info
  *   API metadata
  * @param servers
  *   Array of servers that provide the API
  * @param tags
  *   Tags used to organize the API
  * @param paths
  *   Paths and operations available as APIs
  * @param components
  *   An objects to use in the API
  */
final case class OpenApiUI(
  openapi:    String                         = "3.0.3",
  info:       Info,
  servers:    List[Server]                   = List.empty,
  tags:       Set[Tag]                       = Set.empty,
  paths:      Map[String, Map[String, Path]] = Map.empty,
  components: Option[Component]              = None
)

private[lepus] object OpenApiUI:

  def build(
    info:      Info,
    paths:     Map[String, Map[String, Path]],
    tags:      Set[Tag],
    component: Option[Component]
  ): OpenApiUI =
    OpenApiUI(
      info       = info,
      paths      = paths,
      tags       = tags,
      components = component
    )

/** @param title
  *   API Title
  * @param version
  *   The version of the API design document you are describing
  * @param description
  *   Description of the API
  * @param termsOfService
  * @param contact
  * @param license
  */
private[lepus] final case class Info(
  title:          String,
  version:        String,
  description:    Option[String]  = None,
  termsOfService: Option[String]  = None,
  contact:        Option[Contact] = None,
  license:        Option[License] = None
)

private[lepus] final case class Contact(
  name:  Option[String] = None,
  email: Option[String] = None,
  url:   Option[String] = None
)

private[lepus] final case class License(
  name: String,
  url:  String
)

/** Define the server that is serving the API. For the development environment, define localhost, and for other
  * environments, define staging, production, etc.
  *
  * @param url
  *   The server address that provides the API.
  * @param description
  *   Additional information about the servers provided
  */
private[lepus] final case class Server(
  url:         String,
  description: Option[String] = None
)

private[lepus] final case class Component(
  schemas: ListMap[String, Either[Reference, OpenApiSchema]]
)
