/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.model

import lepus.router.http.Endpoint
import lepus.swagger.SchemaToOpenApiSchema
import lepus.swagger.internal.EndpointMagnet

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
  in:          Parameter.ParameterInType,
  required:    Boolean,
  schema:      Either[Reference, OpenApiSchema],
  description: Option[String]
)

private[lepus] object Parameter:

  def fromEndpoint(
    magnet:                EndpointMagnet,
    schemaToOpenApiSchema: SchemaToOpenApiSchema
  ): magnet.ThisType = magnet.toParameter(schemaToOpenApiSchema)

  enum ParameterInType(`type`: String):
    override def toString: String = `type`
    case PATH   extends ParameterInType("path")
    case QUERY  extends ParameterInType("query")
    case HEADER extends ParameterInType("header")
    case COOKIE extends ParameterInType("cookie")
