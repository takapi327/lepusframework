/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logger

import cats.effect.std.Console

/**
 * Object to write out logs using Cats effect's Console.
 *
 * @param console$F$0
 * @tparam F
 *   the effect type.
 */
class ConsoleOutput[F[_]: Console] extends Output[F]:
  override def output(str: String):             F[Unit] = Console[F].println(str)
  override def outputError(str: String):        F[Unit] = Console[F].errorln(str)
  override def outputStackTrace(ex: Throwable): F[Unit] = Console[F].printStackTrace(ex)

object ConsoleOutput:
  def apply[F[_]: Console]: ConsoleOutput[F] = new ConsoleOutput[F]
