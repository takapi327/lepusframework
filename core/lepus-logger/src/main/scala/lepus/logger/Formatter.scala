/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logger

import cats.Show

/**
 * An object that summarizes [[scala.io.AnsiColor]] into an enum.
 */
object Formatter:

  enum Style(val code: String):
    override def toString: String = code
    def show: Show[Style] = Show.fromToString[Style]
    case Bold       extends Style(Console.BOLD)
    case Blink      extends Style(Console.BLINK)
    case Underlined extends Style(Console.UNDERLINED)
    case Reversed   extends Style(Console.REVERSED)
    case Invisible  extends Style(Console.INVISIBLE)
    case Reset      extends Style(Console.RESET)

  enum Foreground(val code: String):
    override def toString: String = code
    def show: Show[Foreground] = Show.fromToString[Foreground]
    case Black   extends Foreground(Console.BLACK)
    case Green   extends Foreground(Console.GREEN)
    case Yellow  extends Foreground(Console.YELLOW)
    case Red     extends Foreground(Console.RED)
    case Bold    extends Foreground(Console.BOLD)
    case Blue    extends Foreground(Console.BLUE)
    case Magenta extends Foreground(Console.MAGENTA)
    case Cyan    extends Foreground(Console.CYAN)
    case White   extends Foreground(Console.WHITE)

  enum Background(val code: String):
    override def toString: String = code
    def show: Show[Background] = Show.fromToString[Background]
    case Black   extends Background(Console.BLACK_B)
    case Red     extends Background(Console.RED_B)
    case Green   extends Background(Console.GREEN_B)
    case Yellow  extends Background(Console.YELLOW_B)
    case Blue    extends Background(Console.BLUE_B)
    case Magenta extends Background(Console.MAGENTA_B)
    case Cyan    extends Background(Console.CYAN_B)
    case White   extends Background(Console.WHITE_B)
