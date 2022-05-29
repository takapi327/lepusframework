/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import lepus.router.http.RequestEndpoint
import lepus.swagger.SchemaToOpenApiSchema
import lepus.swagger.internal.RequestEndpointMagnet

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

  def fromRequestEndpoint(
    magnet:                RequestEndpointMagnet,
    schemaToOpenApiSchema: SchemaToOpenApiSchema
  ): magnet.ThisType = magnet.toParameter(schemaToOpenApiSchema)

  object ParameterInType {
    val PATH   = "path"
    val QUERY  = "query"
    val HEADER = "header"
    val COOKIE = "cookie"
  }
}
