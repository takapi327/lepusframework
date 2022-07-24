/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.effect._
import cats.data.NonEmptyList

import lepus.router._

object HttpApp extends RouterProvider[IO] {

  override def routes: NonEmptyList[RouterConstructor[IO, _]] =
    NonEmptyList.of(HelloRoute)
}
