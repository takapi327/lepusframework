/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.data.NonEmptyList

import cats.effect.IO

import org.legogroup.woof.local.Local

import lepus.router.{ *, given }

import lepus.swagger.*

object HttpApp extends RouterProvider[IO]:

  override def local: IO[StringLocal] = Local.makeIoLocal[List[(String, String)]]

  override def routes = NonEmptyList.of(
    "hello" / bindPath[String]("name") ->> HelloRoute
  )
