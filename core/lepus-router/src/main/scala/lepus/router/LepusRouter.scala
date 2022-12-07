/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router

import scala.annotation.targetName

import com.google.inject.Injector

import cats.data.NonEmptyList

import org.http4s.server.middleware.CORSPolicy

import lepus.app.LepusApp

trait LepusRouter[F[_]] extends LepusApp[F]:

  /** CORS settings applied to all endpoints */
  val cors: Option[CORSPolicy] = None

  /** List of all endpoints to be launched by the application */
  @targetName("LepusRouterRoutes")
  val routes: Injector ?=> NonEmptyList[Routing[F]]
