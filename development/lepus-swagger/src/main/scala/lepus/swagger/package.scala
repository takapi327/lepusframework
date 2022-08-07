/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

import lepus.router.http.RequestEndpoint
import lepus.router.RouterConstructor

package object swagger extends ExtensionMethods:

  type RouteApi[F[_]] = (RequestEndpoint.Endpoint, RouterConstructor[F, ?] & OpenApiConstructor[F, ?])

