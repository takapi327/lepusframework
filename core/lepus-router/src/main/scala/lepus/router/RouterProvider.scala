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
  val Post    = RequestMethod.Post
  val Put     = RequestMethod.Put
  val Delete  = RequestMethod.Delete
  val Options = RequestMethod.Options
  val Patch   = RequestMethod.Patch
  val Connect = RequestMethod.Connect
  val Trace   = RequestMethod.Trace

  val routes: NonEmptyList[ServerRoute[IO, _]]
}
