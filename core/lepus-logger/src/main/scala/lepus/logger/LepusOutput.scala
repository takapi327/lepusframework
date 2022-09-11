/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logger

import cats.effect.std.Console

import org.legogroup.woof.Output

trait LepusOutput[F[_]] extends Output[F]:
  def outputStackTrace(exception: Throwable): F[Unit]

object LepusOutput:
  def fromConsole[F[_]: Console]: LepusOutput[F] = new LepusOutput[F]:
    override def output(str: String):                    F[Unit] = Console[F].println(str)
    override def outputError(str: String):               F[Unit] = Console[F].errorln(str)
    override def outputStackTrace(exception: Throwable): F[Unit] = Console[F].printStackTrace(exception)
