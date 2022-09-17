/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logger

/**
 * [[org.legogroup.woof.Output]] extension object. A method has been added to Output to export StackTrace.
 * copied from woof:
 * https://github.com/LEGO/woof/blob/main/modules/core/shared/src/main/scala/org/legogroup/woof/Output.scala
 *
 * @tparam F
 *   the effect type.
 */
trait Output[F[_]]:
  def output(str: String):                    F[Unit]
  def outputError(str: String):               F[Unit]
  def outputStackTrace(exception: Throwable): F[Unit]
