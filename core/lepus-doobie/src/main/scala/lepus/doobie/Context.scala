/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie

import cats.effect.IO

/** Trait to incorporate Transactor into the DI of guice
  */
trait Context[F[_]]:
  val xa: Transactor[F]

class ContextIO(val xa: Transactor[IO]) extends Context[IO]
