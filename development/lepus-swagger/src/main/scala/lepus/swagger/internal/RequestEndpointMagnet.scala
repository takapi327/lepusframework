/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.swagger.internal

import lepus.router.http.RequestEndpoint

import lepus.swagger.model.Parameter
import lepus.swagger.SchemaToOpenApiSchema
import Parameter.ParameterInType

trait RequestEndpointMagnet {
  type ThisType
  def toParameter(schemaToOpenApiSchema: SchemaToOpenApiSchema): ThisType
}

object RequestEndpointMagnet {

  implicit def pathMagnet(
    endpoint: RequestEndpoint.Path with RequestEndpoint.Param,
  ): RequestEndpointMagnet = new RequestEndpointMagnet {
    override type ThisType = Parameter

    override def toParameter(schemaToOpenApiSchema: SchemaToOpenApiSchema): ThisType = Parameter(
      name        = endpoint.name,
      in          = ParameterInType.PATH,
      required    = true,
      schema      = schemaToOpenApiSchema(endpoint.converter.schema, false, false),
      description = endpoint.description
    )
  }

  implicit def queryMagnet(
    endpoint: RequestEndpoint.Query with RequestEndpoint.Param,
  ): RequestEndpointMagnet = new RequestEndpointMagnet {
    override type ThisType = Parameter

    override def toParameter(schemaToOpenApiSchema: SchemaToOpenApiSchema): ThisType = Parameter(
      name        = endpoint.key,
      in          = ParameterInType.QUERY,
      required    = false,
      schema      = schemaToOpenApiSchema(endpoint.converter.schema, false, false),
      description = endpoint.description
    )
  }
}
