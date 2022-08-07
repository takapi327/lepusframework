/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.effect.IO
import cats.data.NonEmptyList

import lepus.router.*

import lepus.swagger.OpenApiProvider

object HttpApp extends RouterProvider[IO], OpenApiProvider[IO]:

  override def routes =
    NonEmptyList.of(HelloRoute)
