/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger.internal

import lepus.router.http.Endpoint.*

import lepus.swagger.model.Parameter
import lepus.swagger.SchemaToOpenApiSchema

import Parameter.ParameterInType

trait EndpointMagnet:
  type ThisType
  def toParameter(schemaToOpenApiSchema: SchemaToOpenApiSchema): ThisType

private[lepus] object EndpointMagnet:

  type PathParam  = Path[?] & Param[?]
  type QueryParam = Query[?] & Param[?]

  given Conversion[PathParam, EndpointMagnet] with
    override def apply(endpoint: PathParam): EndpointMagnet =
      new EndpointMagnet {
        override type ThisType = Parameter

        override def toParameter(schemaToOpenApiSchema: SchemaToOpenApiSchema): ThisType = Parameter(
          name        = endpoint.name,
          in          = ParameterInType.PATH,
          required    = endpoint.required,
          schema      = schemaToOpenApiSchema(endpoint.converter.schema, false, false),
          description = endpoint.description
        )
      }

  given Conversion[QueryParam, EndpointMagnet] with
    override def apply(endpoint: QueryParam): EndpointMagnet =
      new EndpointMagnet {
        override type ThisType = Parameter

        override def toParameter(schemaToOpenApiSchema: SchemaToOpenApiSchema): ThisType = Parameter(
          name        = endpoint.key,
          in          = ParameterInType.QUERY,
          required    = endpoint.required,
          schema      = schemaToOpenApiSchema(endpoint.converter.schema, false, false),
          description = endpoint.description
        )
      }
