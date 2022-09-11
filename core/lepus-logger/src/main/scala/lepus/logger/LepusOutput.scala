/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import cats.effect.std.Console

import org.legogroup.woof.Output

/** [[org.legogroup.woof.Output]] extension object. A method has been added to Output to export StackTrace.
  *
  * @tparam F
  *   the effect type.
  */
trait LepusOutput[F[_]] extends Output[F]:
  def outputStackTrace(exception: Throwable): F[Unit]

object LepusOutput:
  /** Methods to export logs using the [[cats.effect.std.Console]] of the cats Effect. */
  def fromConsole[F[_]: Console]: LepusOutput[F] = new LepusOutput[F]:
    override def output(str: String):                    F[Unit] = Console[F].println(str)
    override def outputError(str: String):               F[Unit] = Console[F].errorln(str)
    override def outputStackTrace(exception: Throwable): F[Unit] = Console[F].printStackTrace(exception)
