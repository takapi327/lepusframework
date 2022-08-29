/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package server.router

import cats.data.NonEmptyList

import cats.effect.IO

import org.http4s.Response
import org.http4s.dsl.io.*

import lepus.router.{ *, given }

object HttpApp extends RouterProvider[IO]:

  override def routes = NonEmptyList.of(
    "hello" / bindPath[String]("name") ->> RouterConstructor.of {
      case GET => Ok(s"hello ${summon[String]}")
    }
  )
