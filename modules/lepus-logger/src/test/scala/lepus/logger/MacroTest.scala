/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import org.specs2.mutable.Specification

object MacroTest extends Specification:
  "Testing the Macro" should {
    "The file name matches the one specified." in {
      val exec: ExecLocation = summon[ExecLocation]
      exec.fileName === "MacroTest.scala"
    }

    "The file name does not match the one specified." in {
      val exec: ExecLocation = summon[ExecLocation]
      exec.fileName !== "Test.scala"
    }

    "The enclosureName matches the one specified." in {
      val exec: ExecLocation = summon[ExecLocation]
      exec.enclosureName === "lepus.logger.MacroTest$"
    }

    "The enclosureName does not match the one specified." in {
      val exec: ExecLocation = summon[ExecLocation]
      exec.enclosureName !== "lepus.logger.Test$"
    }

    "The packageName matches the one specified." in {
      val exec: ExecLocation = summon[ExecLocation]
      exec.packageName === "lepus.logger"
    }

    "The packageName does not match the one specified." in {
      val exec: ExecLocation = summon[ExecLocation]
      exec.packageName !== "lepus.test"
    }

    "The lineNumber matches the one specified." in {
      val exec: ExecLocation = summon[ExecLocation]
      exec.lineNumber === 42
    }

    "The lineNumber does not match the one specified." in {
      val exec: ExecLocation = summon[ExecLocation]
      exec.lineNumber !== 23
    }
  }
