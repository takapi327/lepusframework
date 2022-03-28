/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router

import cats.data.NonEmptyList
import cats.effect.IO

import lepus.router.http._

trait RouterProvider {

  val GET     = RequestMethod.Get
  val HEAD    = RequestMethod.Head
  val POST    = RequestMethod.Post
  val PUT     = RequestMethod.Put
  val DELETE  = RequestMethod.Delete
  val OPTIONS = RequestMethod.Options
  val PATCH   = RequestMethod.Patch
  val CONNECT = RequestMethod.Connect
  val TRACE   = RequestMethod.Trace

  val routes: NonEmptyList[ServerRoute[IO, _]]
}
