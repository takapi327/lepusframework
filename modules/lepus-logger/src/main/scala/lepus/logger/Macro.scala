/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import java.io.File
import java.nio.file.Paths

import scala.annotation.tailrec
import scala.quoted.*

/** Macro to obtain information on the execution location where ExecLocation was summoned.
  *
  * copied from woof:
  * https://github.com/LEGO/woof/blob/main/modules/core/shared/src/main/scala/org/legogroup/woof/Macro.scala
  */
object Macro:

  private given ToExpr[File] with
    def apply(f: File)(using Quotes): Expr[File] =
      val path = Expr(f.getAbsolutePath)
      '{ new java.io.File($path) }

  @tailrec
  private def enclosingClass(using q: Quotes)(symb: quotes.reflect.Symbol): quotes.reflect.Symbol =
    if symb.isClassDef then symb else enclosingClass(symb.owner)

  private def execLocation(using Quotes): Expr[ExecLocation] =
    import quotes.reflect.*

    val cls         = enclosingClass(Symbol.spliceOwner)
    val name        = cls.fullName
    val parts       = name.split('.').toList
    val nameExpr    = Expr(name)
    val packageExpr = Expr(parts.dropRight(1).mkString("."))

    val position = Position.ofMacroExpansion
    val filePath =
      if position.sourceFile.getJPath != null then
        position.sourceFile.getJPath.getOrElse(
          quotes.reflect.report.errorAndAbort("Couldn't find file path of the current file")
        )
      else Paths.get(".")
    val lineNumber = Expr(position.startLine + 1)
    val file       = Expr(filePath.getFileName.toString.split("/").takeRight(1).mkString)

    '{ ExecLocation($file, $nameExpr, $packageExpr, $lineNumber) }

  inline given ExecLocation = ${ execLocation }

export Macro.given_ExecLocation
