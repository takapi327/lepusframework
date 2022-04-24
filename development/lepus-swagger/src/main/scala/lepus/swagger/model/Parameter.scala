/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.swagger.model

import io.circe._
import io.circe.generic.semiauto._

import lepus.router.model.Schema
import lepus.router.http.RequestEndpoint

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

  def fromRequestEndpoint(endpoint: RequestEndpoint.Path with RequestEndpoint.Param): Parameter =
    Parameter(
      name        = endpoint.name,
      in          = ParameterInType.PATH,
      required    = true,
      schema      = endpoint.converter.schema,
      description = endpoint.description,
    )

  def fromRequestEndpoint(endpoint: RequestEndpoint.Query with RequestEndpoint.Param): Parameter =
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
