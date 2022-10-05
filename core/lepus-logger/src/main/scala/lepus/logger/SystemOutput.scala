/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

class SystemOutput extends Output:
  override def output(str: String):             Unit = System.out.println(str)
  override def outputError(str: String):        Unit = System.err.println(str)
  override def outputStackTrace(ex: Throwable): Unit = ex.printStackTrace()

object SystemOutput:
  def apply: SystemOutput = new SystemOutput
