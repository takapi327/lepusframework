/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import cats.Show
import cats.syntax.show.*

trait Color:
  def code: String

/** An object that summarizes [[scala.io.AnsiColor]] into an enum.
  */
object Color:
  enum Style(val code: String) extends Color:
    override def toString: String = code
    case Bold       extends Style(Console.BOLD)
    case Blink      extends Style(Console.BLINK)
    case Underlined extends Style(Console.UNDERLINED)
    case Reversed   extends Style(Console.REVERSED)
    case Invisible  extends Style(Console.INVISIBLE)
    case Reset      extends Style(Console.RESET)

  enum Foreground(val code: String) extends Color:
    override def toString: String = code
    case Black   extends Foreground(Console.BLACK)
    case Green   extends Foreground(Console.GREEN)
    case Yellow  extends Foreground(Console.YELLOW)
    case Red     extends Foreground(Console.RED)
    case Bold    extends Foreground(Console.BOLD)
    case Blue    extends Foreground(Console.BLUE)
    case Magenta extends Foreground(Console.MAGENTA)
    case Cyan    extends Foreground(Console.CYAN)
    case White   extends Foreground(Console.WHITE)

  enum Background(val code: String) extends Color:
    override def toString: String = code
    case Black   extends Background(Console.BLACK_B)
    case Red     extends Background(Console.RED_B)
    case Green   extends Background(Console.GREEN_B)
    case Yellow  extends Background(Console.YELLOW_B)
    case Blue    extends Background(Console.BLUE_B)
    case Magenta extends Background(Console.MAGENTA_B)
    case Cyan    extends Background(Console.CYAN_B)
    case White   extends Background(Console.WHITE_B)

  case class Composite(code: String) extends Color:
    override def toString: String = code

  given Show[Style]      = Show.fromToString[Style]
  given Show[Foreground] = Show.fromToString[Foreground]
  given Show[Background] = Show.fromToString[Background]
  given Show[Composite]  = Show.fromToString[Composite]

  extension (color: Color)
    def withStyle(style: Style):                Composite = Composite(color.code + style.code)
    def withBackground(background: Background): Composite = Composite(color.code + background.code)

export Color.given_Show_Style
export Color.given_Show_Foreground
export Color.given_Show_Background
export Color.given_Show_Composite
