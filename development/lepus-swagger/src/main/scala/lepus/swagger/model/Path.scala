/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.swagger.model

import io.circe._
import io.circe.generic.semiauto._

import lepus.router.model.{ Endpoint, Schema }
import lepus.router.http.RequestEndpoint

final case class Path(
  summary:     Option[String]        = None,
  description: Option[String]        = None,
  tags:        List[String]          = List.empty,
  deprecated:  Option[Boolean]       = None,
  parameters:  List[Parameter]       = List.empty,
  //requestBody: Option[RequestBody]  = None,
  responses:   Map[String, Response] = Map.empty,
  //security:    Option[Security]        = None
)

object Path {
  implicit lazy val encoder: Encoder[Path] = deriveEncoder

  def fromEndpoint(endpoint: Endpoint): Path = {
    val parameters = endpoint.endpoint.asVector().flatMap {
      case e: RequestEndpoint.PathParam[_]  => Some(Parameter.fromRequestEndpoint(e))
      case e: RequestEndpoint.QueryParam[_] => Some(Parameter.fromRequestEndpoint(e))
      case _                                => None
    }.toList

    Path(
      summary     = endpoint.summary,
      description = endpoint.description,
      tags        = List.empty,
      parameters  = parameters
    )
  }
}

/**
 * Model representing parameters given to Http requests.
 *
 * @param name        parameter identifier
 * @param in          Classification of this parameter
 * @param required    Value of whether this parameter is required at Http request time.
 * @param schema      This parameter type
 * @param description Description of this parameter
 */
final case class Parameter(
  name:        String,
  in:          String,
  required:    Boolean,
  schema:      Schema,
  description: Option[String],
)

object Parameter {
  implicit lazy val encoder: Encoder[Parameter] = deriveEncoder

  def fromRequestEndpoint(endpoint: RequestEndpoint.PathParam[_]): Parameter =
    Parameter(
      name        = endpoint.name,
      in          = ParameterInType.PATH,
      required    = true,
      schema      = endpoint.converter.schema,
      description = endpoint.description,
    )

  def fromRequestEndpoint(endpoint: RequestEndpoint.QueryParam[_]): Parameter =
    Parameter(
      name        = endpoint.key,
      in          = ParameterInType.QUERY,
      required    = false,
      schema      = endpoint.converter.schema,
      description = endpoint.description,
    )

  object ParameterInType {
    val PATH   = "path"
    val QUERY  = "query"
    val HEADER = "header"
    val COOKIE = "cookie"
  }
}

final case class Response()
object Response {
  implicit lazy val encoder: Encoder[Response] = deriveEncoder
}
