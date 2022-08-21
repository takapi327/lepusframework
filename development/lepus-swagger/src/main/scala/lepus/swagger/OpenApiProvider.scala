/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import cats.data.NonEmptyList

import lepus.router.{ RouterConstructor, RouterProvider }

trait OpenApiProvider[F[_]] extends RouterProvider[F]:
  override def routes: NonEmptyList[RouteApi[F]]
