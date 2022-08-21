/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.internal

import lepus.router.http.RequestEndpoint.*

import lepus.swagger.model.Parameter
import lepus.swagger.SchemaToOpenApiSchema

import Parameter.ParameterInType

trait RequestEndpointMagnet:
  type ThisType
  def toParameter(schemaToOpenApiSchema: SchemaToOpenApiSchema): ThisType

private[lepus] object RequestEndpointMagnet:

  type PathParam  = Path[?] & Param[?]
  type QueryParam = Query[?] & Param[?]

  given Conversion[PathParam, RequestEndpointMagnet] with
    override def apply(endpoint: PathParam): RequestEndpointMagnet =
      new RequestEndpointMagnet {
        override type ThisType = Parameter

        override def toParameter(schemaToOpenApiSchema: SchemaToOpenApiSchema): ThisType = Parameter(
          name        = endpoint.name,
          in          = ParameterInType.PATH,
          required    = true,
          schema      = schemaToOpenApiSchema(endpoint.converter.schema, false, false),
          description = endpoint.description
        )
      }

  given Conversion[QueryParam, RequestEndpointMagnet] with
    override def apply(endpoint: QueryParam): RequestEndpointMagnet =
      new RequestEndpointMagnet {
        override type ThisType = Parameter

        override def toParameter(schemaToOpenApiSchema: SchemaToOpenApiSchema): ThisType = Parameter(
          name        = endpoint.key,
          in          = ParameterInType.QUERY,
          required    = false,
          schema      = schemaToOpenApiSchema(endpoint.converter.schema, false, false),
          description = endpoint.description
        )
      }
