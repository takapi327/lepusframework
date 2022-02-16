/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus

import cats.Semigroup
import cats.syntax.semigroupk._

import cats.effect.IO

import org.http4s.HttpRoutes

package object router extends LepusRouter with ExtensionMethods {
  implicit val routesSemigroup: Semigroup[HttpRoutes[IO]] = _ combineK _
}
