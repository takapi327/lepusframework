/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router

import lepus.router.model.{ DecodeRequestResult, ServerRequest }

object RequestDecodeHandler {

  def handleRequest[F[_], T](
    serverRequest: ServerRequest[F],
    endpoint:      LepusEndpoint[F, _, T]
  ): DecodeRequestResult = {
    val (decodeEndpointResult, _) = DecodeEndpoint(serverRequest, endpoint.endpoint)
    decodeEndpointResult match {
      case _: DecodeEndpointResult.Failure      => DecodeRequestResult.Failure()
      case DecodeEndpointResult.Success(values) => DecodeRequestResult.Success(values)
    }
  }
}
