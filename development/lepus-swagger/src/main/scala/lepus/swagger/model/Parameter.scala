/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import lepus.router.http.RequestEndpoint
import lepus.swagger.SchemaToOpenApiSchema

/** Model representing parameters given to Http requests.
  *
  * @param name
  *   parameter identifier
  * @param in
  *   Classification of this parameter
  * @param required
  *   Value of whether this parameter is required at Http request time.
  * @param schema
  *   This parameter type
  * @param description
  *   Description of this parameter
  */
final case class Parameter(
  name:        String,
  in:          String,
  required:    Boolean,
  schema:      Either[Reference, OpenApiSchema],
  description: Option[String]
)

object Parameter {

  val schemaToOpenApiSchema = new SchemaToOpenApiSchema()

  def fromRequestEndpoint(endpoint: RequestEndpoint.Path with RequestEndpoint.Param): Parameter =
    Parameter(
      name        = endpoint.name,
      in          = ParameterInType.PATH,
      required    = true,
      schema      = schemaToOpenApiSchema(endpoint.converter.schema),
      description = endpoint.description
    )

  def fromRequestEndpoint(endpoint: RequestEndpoint.Query with RequestEndpoint.Param): Parameter =
    Parameter(
      name        = endpoint.key,
      in          = ParameterInType.QUERY,
      required    = false,
      schema      = schemaToOpenApiSchema(endpoint.converter.schema),
      description = endpoint.description
    )

  object ParameterInType {
    val PATH   = "path"
    val QUERY  = "query"
    val HEADER = "header"
    val COOKIE = "cookie"
  }
}
